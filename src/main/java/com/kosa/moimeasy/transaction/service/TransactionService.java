package com.kosa.moimeasy.transaction.service;

import com.kosa.moimeasy.common.exception.ResourceNotFoundException;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.security.provider.JwtTokenProvider;
import com.kosa.moimeasy.transaction.dto.*;
import com.kosa.moimeasy.transaction.entity.Transaction;
import com.kosa.moimeasy.transaction.exception.CustomException;
import com.kosa.moimeasy.transaction.repository.TransactionRepository;
import com.kosa.moimeasy.transaction.type.ErrorCode;
import com.kosa.moimeasy.transaction.type.TransactionType;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kosa.moimeasy.transaction.type.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final MoeimRepository moeimRepository;
    private final TransactionRepository transactionRepository;

    // 유저 계좌 입금
    @Transactional
    public DepositDto.Response userDeposit(DepositDto.Request request) {

        // 유저 찾기
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 계좌 금액 변경
        if (request.getAmount() <= 0) {
            throw new CustomException(BALANCE_NOT_ENOUGH); // 예외 처리: 잘못된 금액
        }
        user.setAmount(user.getAmount() + request.getAmount());

        // 거래 내역 저장
        transactionRepository.save(
                Transaction.builder()
                        .userAccount(user)
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(request.getAmount())
                        .depositName(user.getUserName())
                        .build()
        );

        // 응답 생성 및 반환
        return DepositDto.Response.builder()
                .accountNumber(user.getAccountNumber())
                .depositName(user.getUserName())
                .amount(request.getAmount())
                .transacted_at(LocalDateTime.now())
                .build();
    }

    // 모임 계좌 입금
    @Transactional
    public DepositDto.Response moeimDeposit(DepositDto.Request request) {
        Long userId = request.getUserId();

        // 계좌번호 존재(삭제 여부) 확인
        Moeim moeim = moeimRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));

        // 계좌번호 존재(삭제 여부) 확인
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));


        // 계좌가 있다면 입금 금액만큼 잔액 변경
        moeim.setAmount(moeim.getAmount() + request.getAmount());

        // 거래 내역 테이블에 거래추가
        transactionRepository.save(
            Transaction.builder()
                .moeimAccount(moeim)
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .depositName(user.getUserName())
                .build()
        );

        return DepositDto.Response.builder()
            .accountNumber(moeim.getAccountNumber())
            .depositName(user.getUserName())
            .amount(request.getAmount())
            .transacted_at(LocalDateTime.now())
            .build();
    }

    // 유저 정보 반환
    public DetailDto getDetials(Long userId, Long moeimId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(SENT_ACCOUNT_NOT_FOUND));

        Moeim moeim = moeimRepository.findById(moeimId)
                .orElseThrow(() -> new CustomException(RECEIVED_ACCOUNT_NOT_FOUND));

        return DetailDto.builder()
                .userAccount(user.getAccountNumber())
                .userName(user.getUserName())
                .moeimName(moeim.getMoeimName())
                .moeimAccount(moeim.getAccountNumber())
                .build();
    }

    // 모임 계좌 출금
    @Transactional
    public WithdrawDto.Response withdraw(WithdrawDto.Request request) {
        // 출금 요청한 계좌의 존재 여부 확인
        Moeim moeim = moeimRepository.findByAccountNumber(
                request.getAccountNumber())
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));

        // (출금 요청 금액 > 잔액)의 경우 예외 발생
        if (request.getAmount() > moeim.getAmount()) {
            throw new CustomException(BALANCE_NOT_ENOUGH);
        }

        // 출금 요청한 금액만큼 잔액 변경
        moeim.setAmount(moeim.getAmount() - request.getAmount());

        // 거래 내역 테이블에 거래 추가
        transactionRepository.save(
            Transaction.builder()
                .moeimAccount(moeim)
                .transactionType(TransactionType.WITHDRAW)
                .amount(request.getAmount())
                .withdrawName(request.getWithdrawName())
                    .categoryName(request.getCategoryName())
                .build()
        );
        // 반환
        return WithdrawDto.Response.builder()
            .accountNumber(request.getAccountNumber())
            .withdrawName(request.getWithdrawName())
            .amount(request.getAmount())
                .categoryName(request.getCategoryName())
            .transacted_at(LocalDateTime.now())
            .build();
    }

    // 유저에서 모임으로 송금
    @Transactional
    public RemittanceDto.Response remittance(RemittanceDto.Request request) {

        User sentAccount = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new CustomException(SENT_ACCOUNT_NOT_FOUND));

        Moeim receivedAccount = moeimRepository.findById(request.getMoeimId())
            .orElseThrow(() -> new CustomException(RECEIVED_ACCOUNT_NOT_FOUND));

        // (송금 요청 금액 > 보내는 계좌의 잔액)의 경우 예외 발생
        if (request.getAmount() > sentAccount.getAmount()) {
            throw new CustomException(BALANCE_NOT_ENOUGH);
        }

        // 이미 납부했으면 납부했다는 알림
        List<Transaction> transactions = transactionRepository.findByUserId(request.getUserId());

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType().equals(TransactionType.REMITTANCE)) {
                throw new CustomException(USER_ALREADY_PAID);
            }
        }

        // 보내는 계좌, 받는 계좌의 잔액 변경
        sentAccount.setAmount(sentAccount.getAmount() - request.getAmount());
        receivedAccount.setAmount(receivedAccount.getAmount() + request.getAmount());

        // 거래 테이블에 거래 저장
        transactionRepository.save(
            Transaction.builder()
                    .moeimAccount(receivedAccount)
                    .userAccount(sentAccount)
                    .transactionType(TransactionType.REMITTANCE)
                    .amount(request.getAmount())
                    .depositName(sentAccount.getUserName())
                    .receivedName(receivedAccount.getMoeimName())
                    .receivedAccount(receivedAccount.getAccountNumber())
                    .build()
        );

        return RemittanceDto.Response.builder()
                .sentAccountNumber(sentAccount.getAccountNumber())
                .sentName(sentAccount.getUserName())
                .receivedAccountNumber(receivedAccount.getAccountNumber())
                .receivedName(receivedAccount.getMoeimName())
                .amount(request.getAmount())
                .build();
    }

    // 유저 -> 모임 송금내역 조회
    @Transactional
    public TransactionListDto.RemittanceListResponse getRemittanceList(TransactionListDto.Request request) {

        // 모임 아이디 받아서 확인
        Moeim moeim = getValidmoeim(request.getMoeimId());

        // 조회 계좌 존재/삭제 여부 확인
        if(moeimRepository.findByAccountNumber(moeim.getAccountNumber()).isEmpty())
            throw new CustomException(ACCOUNT_NOT_FOUND);


        // 올바르게 요청된 날짜 형식: "yyyy-MM-dd"
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDate nowDate = LocalDate.now();
        int defaultDateRange = 7;
        int maxDateRange = 40;

        // 시작 날짜와 끝 날짜가 null 인 경우 (조회일 포함 일주일 내역 반환)
        if (startDate == null && endDate == null) {
            LocalDate weekAgoDate = nowDate.minusDays(defaultDateRange - 1);

            return getRemittanceListResponse(request.getMoeimId(), weekAgoDate, nowDate);
        }

        // 시작 날짜와 끝 날짜 둘 중 하나만 null 로 보낸 경우
        if (startDate == null || endDate == null) {
            throw new CustomException(INVALID_DATE);
        }

        // ======== 두 날짜 다 null 이 아닌 경우들 ========

        // 시작 날짜가 끝 날짜를 초과한 경우
        if (startDate.isAfter(endDate)) {
            throw new CustomException(INVALID_DATE);
        }

        // 끝 날짜가 조회 당일 날짜를 초과한 경우 오늘 날짜로 반환
        if (endDate.isAfter(nowDate)) {
            return getRemittanceListResponse(request.getMoeimId(), startDate, nowDate);
//            throw new CustomException(INVALID_DATE);
        }

        // 조회 기간이 최대 조회 기간을 넘을 경우
        int betweenDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        if (betweenDays + 1 > maxDateRange) {
            throw new CustomException(INVALID_DATE_RANGE);
        }

        // 두 날짜 전부 제대로 조회한 경우
        return getRemittanceListResponse(request.getMoeimId(), startDate, endDate);
    }

    // 거래내역 조회
    @Transactional
    public TransactionListDto.Response getTransactionList(TransactionListDto.Request request) {

        // 조회 계좌 존재/삭제 여부 확인
        Moeim moeim = getValidmoeim(request.getMoeimId());
        if(moeim.getMoeimId() == null){
            throw new CustomException(ACCOUNT_NOT_FOUND);
        }

        // 올바르게 요청된 날짜 형식: "yyyy-MM-dd"
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDate nowDate = LocalDate.now();
        int defaultDateRange = 7;
        int maxDateRange = 40;

        // 시작 날짜와 끝 날짜가 null 인 경우 (조회일 포함 일주일 내역 반환)
        if (startDate == null && endDate == null) {
            LocalDate weekAgoDate = nowDate.minusDays(defaultDateRange - 1);

            return getTransactionListResponse(request.getMoeimId(), weekAgoDate, nowDate);
        }

        // 시작 날짜와 끝 날짜 둘 중 하나만 null 로 보낸 경우
        if (startDate == null || endDate == null) {
            throw new CustomException(INVALID_DATE);
        }

        // ======== 두 날짜 다 null 이 아닌 경우들 ========

        // 시작 날짜가 끝 날짜를 초과한 경우
        if (startDate.isAfter(endDate)) {
            throw new CustomException(INVALID_DATE);
        }

        // 끝 날짜가 조회 당일 날짜를 초과한 경우 오늘 날짜로 반환
        if (endDate.isAfter(nowDate)) {
            return getTransactionListResponse(request.getMoeimId(), startDate, nowDate);
//            throw new CustomException(INVALID_DATE);
        }

        // 조회 기간이 최대 조회 기간을 넘을 경우
        int betweenDays = (int) ChronoUnit.DAYS.between(startDate, endDate);
        if (betweenDays + 1 > maxDateRange) {
            throw new CustomException(INVALID_DATE_RANGE);
        }

        // 두 날짜 전부 제대로 조회한 경우
        return getTransactionListResponse(moeim.getMoeimId(), startDate, endDate);
    }

    // 모임 확인
    private Moeim getValidmoeim(Long moeimid) {
        return moeimRepository.findById(moeimid)
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));
    }

    // 거래 유형
    private String getTransactionTargetName(Transaction transaction) {
        switch (transaction.getTransactionType()) {
            case WITHDRAW -> {
                return transaction.getCategoryName();
            }
            case DEPOSIT -> {
                return transaction.getDepositName();
            }
            case REMITTANCE -> {
                return transaction.getDepositName();
            }
        }
        return ErrorCode.TRANSACTION_TYPE_NOT_FOUND.getDescription();
    }

    // 거래 내역 조회 Response Dto 반환
    private TransactionListDto.Response getTransactionListResponse(Long moeimId, LocalDate startDate, LocalDate endDate
    ) {
        List<Transaction> resultList = transactionRepository.findByMoeimAccountAndDateRange(
                moeimId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)
        );
        return TransactionListDto.Response.builder()
                .transactionList(resultList.stream()
                        .map(transaction ->
                                TransactionDto.builder()
                                        .id(transaction.getId())
                                        .transactionTargetName(getTransactionTargetName(transaction))
                                        .amount(transaction.getAmount())
                                        .type(determineTransactionType(transaction))
                                        .transactedAt(transaction.getTransactedAt())
                                    .build()
                ).toList())
            .build();
    }

    private String determineTransactionType(Transaction transaction) {
        if (transaction.getTransactionType()== TransactionType.WITHDRAW) {
            return "출금";
        } else {
            return "입금";
        }
    }
    // 송금 내역 조회 Response Dto 반환
    public TransactionListDto.RemittanceListResponse getRemittanceListResponse(Long moeimId, LocalDate startDate, LocalDate endDate) {

        // 모임 거래내역
        List<Transaction> resultList = transactionRepository.findAllByMoeimId(moeimId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

        // 모임에 가입된 유저
        List<User> users = userRepository.findByMoeimId(moeimId);

        // 송금내역 있는 사용자들에 대한 DTO 변환
        List<RemittanceListDto> remittanceList = resultList.stream()
                .map(transaction -> {
                    Long userId = null;
                    String userName = "알 수 없는 사용자";
                    String photo = "default-photo.png";
                    if (transaction.getUserAccount() != null) {
                        userId = transaction.getUserAccount().getUserId();
                        userName = transaction.getUserAccount().getUserName();
                        photo = transaction.getUserAccount().getProfileImage();
                    }

                    return RemittanceListDto.builder()
                            .userId(userId)
                            .receivedAccount(transaction.getReceivedAccount())
                            .photo(photo)
                            .userName(userName)
                            .amount(transaction.getAmount())
                            .transactionType(TransactionType.REMITTANCE)
                            .transactionAt(transaction.getTransactedAt())
                            .build();
                })
                .collect(Collectors.toList());

        Set<Long> userWithTransactions = remittanceList.stream()
                .map(RemittanceListDto::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

// 거래내역 없는 유저 추가
        for (User user : users) {
            if (!userWithTransactions.contains(user.getUserId())) {
                String userName = user.getNickname() != null ? user.getNickname() : "알 수 없는 사용자";
                String photo = user.getProfileImage();
                remittanceList.add(RemittanceListDto.builder()
                        .userId(user.getUserId())
                        .receivedAccount("알 수 없는 계좌")
                        .photo(photo)
                        .userName(userName)
                        .amount(0.0)
                        .transactionType(null)
                        .transactionAt(null)
                        .build());
            }
        }

        return TransactionListDto.RemittanceListResponse.builder()
                .remittanceList(remittanceList)
                .build();
    }


    // 카테고리 반환
    public InitialDataDto getInitialData(InitialDataDto request) {

        //사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자에 해당하는 모임 조회 (사용자의 ID를 기반으로)
        Moeim moeim = moeimRepository.findByUserId(user.getMoeimId())
                .orElseThrow(() -> new ResourceNotFoundException("모임을 찾을 수 없습니다."));

        LocalDate startDate = request.getStartDate(); // 월의 시작일 2024-12-01T00:00
        LocalDate endDate = request.getEndDate(); // 월의 마지막일 2024-12-31T23:59:59

        // 레포지토리에서 사용하기 위해 LocalDateTime 변환
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 2024-12-01T00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 2024-12-31T23:59:59


        // 모임에 해당하는 거래 데이터 조회 (모임 ID를 기반으로)
        List<Transaction> transactions = transactionRepository.findCategoryNameByMoeimId(moeim.getMoeimId(), startDateTime, endDateTime);

        // double 타입으로 합산
        Map<String, Double> categoryMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        tr -> tr.getCategoryName(),
                        Collectors.mapping(
                                tr -> tr.getAmount(), // 이미 double 반환
                                Collectors.reducing(0.0, (a, b) -> a + b) // double 덧셈
                        )
                ));

        // 카테고리별 DTO 리스트 생성
        List<CategoryDto> categories = categoryMap.entrySet().stream()
                .map(entry -> CategoryDto.builder()
                        .categoryName(entry.getKey())
                        .categoryMoney(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        // 초기 데이터 DTO 생성 및 반환
        return InitialDataDto.builder()
                .userName(user.getUserName())
                .userAccount(user.getAccountNumber())
                .moeimName(moeim.getMoeimName())
                .moeimAccount(moeim.getAccountNumber())
                .startDate(startDate)
                .endDate(endDate)
                .categories(categories)
                .build();
    }
}
