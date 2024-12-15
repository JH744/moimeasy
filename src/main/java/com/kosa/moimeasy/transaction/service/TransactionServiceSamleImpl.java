//package com.kosa.moimeasy.transaction.service;
//
//import com.kosa.moimeasy.common.exception.NotExistMoeimAccountException;
//import com.kosa.moimeasy.moeim.entity.Moeim;
//import com.kosa.moimeasy.moeim.repository.MoeimRepository;
//import com.kosa.moimeasy.transaction.dto.GetTransactionByCategoryResponseDTO;
//import com.kosa.moimeasy.transaction.dto.GetTransactionResponseDTO;
//import com.kosa.moimeasy.transaction.entity.TransactionSample;
//import com.kosa.moimeasy.transaction.repository.TransactionRepositorySample;
//import com.kosa.moimeasy.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class TransactionServiceSamleImpl implements TransactionServiceSamle {
//
//    private final TransactionRepositorySample transactionRepositorySample;
//    private final MoeimRepository moeimRepository;
//    private final UserRepository userRepository;
//
//    // 거래내역 저장
//    @Override
//    public void registTransactionInfo(TransactionSample transactionSample) {
//        transactionRepositorySample.save(transactionSample);
//    }
//
//    // 모임의 전체 거래내역 조회
//    @Override
//    public List<TransactionSample> findByMoeimId(Long moeimId) {
//        return transactionRepositorySample.findByMoeim_Id(moeimId);
//    }
//
//    // 특정 시점 이후 모임 계좌의 거래 내역을 조회
//    @Override
//    public List<TransactionSample> findTransactionsAfter(Moeim moeim, LocalDateTime transactionAt) {
//        return transactionRepositorySample.findByTransactionAtList(transactionAt, moeim.getAccountNumber());
//    }
//
//    // 특정 시점 이후 발생한 모임 계좌의 출금 내역 조회
//    @Override
//    public List<TransactionSample> findWithdrawalsAfter(Moeim moeim, LocalDateTime transactionAt) {
//         return transactionRepositorySample.findByMoeimAndTransactionAtAfterAndWithdrawalAmountGreaterThan(moeim, transactionAt);
//    }
//
//    // 모임 계좌의 카테고리 반환
//    @Override
//    public int findTransactionOne(Long moeimId) {
//        Moeim moeimAccount = moeimRepository.findAccountNumberById(moeimId).orElseThrow(NotExistMoeimAccountException::new);
//        TransactionSample transactionSample = transactionRepositorySample.findFirstByMoeim(moeimAccount);
//        int category = 0;
//        if(transactionSample != null) {
//            String categoryName = transactionSample.getCategoryName();
//            if(categoryName.equals("회식")) category = 1;
//            if(categoryName.equals("음료")) category = 2;
//            if(categoryName.equals("운영")) category = 3;
//            if(categoryName.equals("시설대여")) category = 4;
//            if(categoryName.equals("기타")) category = 5;
//        }
//        return category;
//    }
//
//    // 모임 계좌의 전체 거래내역 조회(페이지 네이션)
//    @Override
//    public List<GetTransactionResponseDTO> getTransactionList(Long moeimId, int idx) {
//        log.info("모임 계좌의 거래내역 조회(5건씩)");
//        Pageable pageable = PageRequest.of(idx, 5, Sort.by(Sort.Order.desc("createAt")));
//        Page<TransactionSample> transactions = transactionRepositorySample.findByMoeim(moeimId, pageable);
//        return transactions.getContent().stream().map(transaction -> transaction.toGetTransactionResponseDto()).toList();
//    }
//
//    // 카테고리 별 거래 통계 계산
//    @Override
//    public List<GetTransactionByCategoryResponseDTO> getTransactionListByCategory(Long moeimId, int year, int month) {
//        log.info("계좌의 특정 달의 카테고리별 통계 정보 ");
//        // 특정년,달 계좌의 거래내역 조회
//        // 카테고리별로 액수 구분
//        HashMap<String,Integer> categoryMap = new HashMap<>();
//        AtomicInteger totalAmount = new AtomicInteger(0);
//        transactionRepositorySample.findByMoeimAndYearAndMonth(moeimId, year, month)
//                .stream().forEach(transaction -> {
//                    categoryMap.put(transaction.getCategoryName(), categoryMap.getOrDefault(transaction.getCategoryName(), 0) + (int)transaction.getWithdrawalAmount());
//                    totalAmount.addAndGet((int)transaction.getWithdrawalAmount());
//                });
//
//        List<GetTransactionByCategoryResponseDTO> dtoList = new ArrayList<>();
//        for (String category : categoryMap.keySet()) {
//            dtoList.add(GetTransactionByCategoryResponseDTO.builder()
//                    .amount(categoryMap.get(category))
//                    .category(category)
//                    .rate(Math.round(((double) categoryMap.get(category) / totalAmount.get()) * 100 * 10.0) / 10.0)
//                    .build());
//        }
//
//        return dtoList.stream()
//                .sorted(Comparator.comparing(GetTransactionByCategoryResponseDTO::getAmount).reversed())
//                .collect(Collectors.toList());
//    }
//
////    @Override
////    public List<Transaction> findTransactionsByTypeWithinPeriod(String accountNumber, LocalDateTime start, LocalDateTime end, int transactionType) {
////        return List.of();
////    }
//}
