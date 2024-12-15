package com.kosa.moimeasy.transaction.service;

import static com.kosa.moimeasy.transaction.type.ErrorCode.ACCOUNT_NOT_FOUND;
import static com.kosa.moimeasy.transaction.type.ErrorCode.BALANCE_NOT_ENOUGH;
import static com.kosa.moimeasy.transaction.type.ErrorCode.INVALID_DATE;
import static com.kosa.moimeasy.transaction.type.ErrorCode.INVALID_DATE_RANGE;
import static com.kosa.moimeasy.transaction.type.ErrorCode.NOT_EQUAL_ID_AND_ACCOUNT_NUMBER;
import static com.kosa.moimeasy.transaction.type.ErrorCode.RECEIVED_ACCOUNT_NOT_FOUND;
import static com.kosa.moimeasy.transaction.type.ErrorCode.SENT_ACCOUNT_NOT_FOUND;
import static com.kosa.moimeasy.transaction.type.ErrorCode.TOKEN_NOT_MATCH_USER;

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

import javax.naming.directory.ModificationItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final MoeimRepository moeimRepository;
    private final TransactionRepository transactionRepository;

    private final JwtTokenProvider tokenProvider;

    // 유저 계좌 입금
    @Transactional
    public DepositDto.Response userDeposit(String token, DepositDto.Request request) {
        Long tokenUserId = tokenProvider.getUserIdFromJWT(token);

        // 동일한 회원인지
        if (!Objects.equals(tokenUserId, userRepository.findById(tokenUserId))) {
            throw new CustomException(TOKEN_NOT_MATCH_USER);
        }

        // 토큰으로 회원 아이디 확인
        User user = userRepository
                .findById(tokenUserId)
                .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));

        // 계좌가 있다면 입금 금액만큼 잔액 변경
        user.setAmount(user.getAmount() + request.getAmount());

        // 거래 내역 테이블에 거래추가
        transactionRepository.save(
                Transaction.builder()
                        .userAccount(user)
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(request.getAmount())
                        .depositName(user.getUserName())
                        .build()
        );

        return DepositDto.Response.builder()
                .accountNumber(user.getAccountNumber())
                .depositName(user.getUserName())
                .amount(request.getAmount())
                .transacted_at(LocalDateTime.now())
                .build();
    }


    // 모임 계좌 입금
    @Transactional
    public DepositDto.Response moeimDeposit(String token, DepositDto.Request request) {
        Long tokenUserId = tokenProvider.getUserIdFromJWT(token);

        // 회원의 토큰값과 모임의 유저아이디가 같은지
        if (!Objects.equals(tokenUserId, moeimRepository.findByUserId(tokenUserId))) {
            throw new CustomException(TOKEN_NOT_MATCH_USER);
        }

        // 계좌번호 존재(삭제 여부) 확인
        Moeim moeim = moeimRepository
            .findByUserId(tokenUserId)
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));

        // 계좌번호 존재(삭제 여부) 확인
        User user = userRepository
                .findById(tokenUserId)
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

    // 토큰으로 모임, 유저 정보 반환
    public DetailDTO getDetials(String token){
        Long tokenUserId = tokenProvider.getUserIdFromJWT(token);

        User user = userRepository.findById(tokenUserId)
                .orElseThrow(() -> new CustomException(SENT_ACCOUNT_NOT_FOUND));

        Moeim moeim = moeimRepository.findByUserId(tokenUserId)
                .orElseThrow(() -> new CustomException(RECEIVED_ACCOUNT_NOT_FOUND));

        return DetailDTO.builder()
                .userAccount(user.getAccountNumber())
                .userName(user.getUserName())
                .moeimName(moeim.getMoeimName())
                .moeimAccount(moeim.getAccountNumber())
                .build();
    }

    // 모임 계좌 출금
    @Transactional
    public WithdrawDto.Response withdraw(String token, WithdrawDto.Request request) {
        // 출금 요청한 계좌의 존재 여부 확인
        Long tokenUserId = tokenProvider.getUserIdFromJWT(token);
        Moeim moeim = moeimRepository.findByAccountNumber(
                request.getAccountNumber())
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));

        // 토큰의 사용자와 출금 요청 계좌의 소유주 일치 여부 확인
        if (!Objects.equals(tokenUserId, moeim.getUser().getUserId())) {
            throw new CustomException(TOKEN_NOT_MATCH_USER);
        }

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
                .build()
        );

        return WithdrawDto.Response.builder()
            .accountNumber(request.getAccountNumber())
            .withdrawName(request.getWithdrawName())
            .amount(request.getAmount())
            .transacted_at(LocalDateTime.now())
            .build();
    }

    // 유저에서 모임으로 송금
    @Transactional
    public RemittanceDto.Response remittance(String token, RemittanceDto.Request request) {
        // 토큰의 사용자와 보내는 계좌의 사용자 확인
        Long tokenUserId = tokenProvider.getUserIdFromJWT(token);
        log.info("tokenUserId:{}", tokenUserId);

        User sentAccount = userRepository.findById(tokenUserId)
            .orElseThrow(() -> new CustomException(SENT_ACCOUNT_NOT_FOUND));

        Moeim receivedAccount = moeimRepository.findByUserId(tokenUserId)
            .orElseThrow(() -> new CustomException(RECEIVED_ACCOUNT_NOT_FOUND));

        if (!Objects.equals(tokenUserId, sentAccount.getUserId())) {
            throw new CustomException(TOKEN_NOT_MATCH_USER);
        }

        // (송금 요청 금액 > 보내는 계좌의 잔액)의 경우 예외 발생
        if (request.getAmount() > sentAccount.getAmount()) {
            throw new CustomException(BALANCE_NOT_ENOUGH);
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
                .receivedName(receivedAccount.getMoeimName())
                .receivedAccount(receivedAccount.getAccountNumber())
                .build()
        );

        return RemittanceDto.Response.builder()
            .sentAccountNumber(sentAccount.getAccountNumber())
            .receivedAccountNumber(receivedAccount.getAccountNumber())
            .receivedName(receivedAccount.getMoeimName())
            .amount(request.getAmount())
            .build();
    }

    // 유저 -> 모임 송금내역 조회
    @Transactional
    public TransactionListDto.RemittanceListResponse getRemittanceList(String token, TransactionListDto.Request request) {

        // 모임 아이디 받아서 확인
        Moeim moeim = getValidmoeim(request.getMoeimId());

        // 토큰의 사용자 id와 거래내역을 조회할 계좌의 userId 확인
        if (!Objects.equals(tokenProvider.getUserIdFromJWT(token), moeim.getUser().getUserId())) {
            throw new CustomException(TOKEN_NOT_MATCH_USER);
        }
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
    public TransactionListDto.Response getTransactionList(String token, TransactionListDto.Request request) {
        // 조회 계좌 존재/삭제 여부 확인
        Moeim moeim = getValidmoeim(request.getMoeimId());

        // 토큰의 사용자 id와 거래내역을 조회할 계좌의 userId 확인
        if (!Objects.equals(tokenProvider.getUserIdFromJWT(token), moeim.getUser().getUserId())) {
            throw new CustomException(TOKEN_NOT_MATCH_USER);
        }

        // 계좌 id 와 계좌번호의 계좌 id 일치 여부 확인
        Transaction transaction = transactionRepository.findById(request.getMoeimId())
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));
        if (!request.getMoeimId().equals(transaction.getMoeimAccount().getMoeimId())) {
            throw new CustomException(NOT_EQUAL_ID_AND_ACCOUNT_NUMBER);
        }

        // 올바르게 요청된 날짜 형식: "yyyy-MM-dd"
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        LocalDate nowDate = LocalDate.now();
        int defaultDateRange = 7;
        int maxDateRange = 40;

        log.info("StartDate: {}, EndDate: {}, MoeimId: {}", startDate, endDate, request.getMoeimId());

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
        return getTransactionListResponse(request.getMoeimId(), startDate, endDate);
    }


    private Moeim getValidmoeim(Long moeimid) {
        Moeim moeim = moeimRepository.findById(moeimid)
            .orElseThrow(() -> new CustomException(ACCOUNT_NOT_FOUND));

        return moeim;
    }


    private String getTransactionTargetName(Transaction transaction) {
        switch (transaction.getTransactionType()) {
            case WITHDRAW -> {
                return transaction.getWithdrawName();
            }
            case DEPOSIT -> {
                return transaction.getDepositName();
            }
            case REMITTANCE -> {
                return transaction.getReceivedName();
            }
        }
        return ErrorCode.TRANSACTION_TYPE_NOT_FOUND.getDescription();
    }

    // 거래 내역 조회 Response Dto 반환
    private TransactionListDto.Response getTransactionListResponse(
        Long moeimId, LocalDate startDate, LocalDate endDate
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
                                    .type(transaction.getTransactionType())
                                    .transactedAt(transaction.getTransactedAt())
                                    .build()
                ).toList())
            .build();
    }

    // 송금 내역 조회 Response Dto 반환
    public TransactionListDto.RemittanceListResponse getRemittanceListResponse(
            Long moeimId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> resultList = transactionRepository.findByMoeimAccountAndDateRange(
                moeimId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)
        );

        return TransactionListDto.RemittanceListResponse.builder()
                .remittanceList(resultList.stream()
                        .map(transaction -> {
                            // UserAccount null 체크
                            Long userId = null;
                            String userName = "알 수 없는 사용자";
                            String photo = "default-photo.png";
                            if (transaction.getUserAccount() != null) {
                                userId = transaction.getUserAccount().getUserId();
                                userName = transaction.getUserAccount().getNickname();
                                if (transaction.getUserAccount().getProfileImage() != null) {
                                    photo = transaction.getUserAccount().getProfileImage();
                                }
                            }

                            // 로그 추가
                            log.info("Processing Transaction: id={}, userId={}, userName={}, photo={}, receivedAccount={}",
                                    transaction.getId(), userId, userName, photo, transaction.getReceivedAccount());

                            // DTO 생성 및 반환
                            return RemittanceListDto.builder()
                                    .userId(userId)
                                    .receivedAccount(transaction.getReceivedAccount() != null
                                            ? transaction.getReceivedAccount()
                                            : "알 수 없는 계좌")
                                    .photo(photo)
                                    .userName(userName)
                                    .amount(transaction.getAmount())
                                    .transactionType(TransactionType.REMITTANCE)
                                    .transactionAt(transaction.getTransactedAt())
                                    .build();
                        }).toList())
                .build();
    }
}