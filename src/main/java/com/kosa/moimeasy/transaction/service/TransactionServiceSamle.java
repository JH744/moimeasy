package com.kosa.moimeasy.transaction.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.transaction.dto.GetTransactionByCategoryResponseDTO;
import com.kosa.moimeasy.transaction.dto.GetTransactionResponseDTO;
import com.kosa.moimeasy.transaction.entity.TransactionSample;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionServiceSamle {

    // 거래 정보를 데이터베이스에 저장
    void registTransactionInfo(TransactionSample transactionSample);

    // 특정 모임 ID를 기반으로 전체 거래 정보 조회
    List<TransactionSample> findByMoeimId(Long moeimId);

    // 모임 계좌의 첫 거래 내역을 조회하고 카테고리 반환
    int findTransactionOne(Long moeimId);

    // 특정 시점 이후 발생한 모임 계좌의 거래 내역 조회
    List<TransactionSample> findTransactionsAfter(Moeim moeim, LocalDateTime transactionAt);

    // 특정 시점 이후 발생한 모임 계좌의 출금 내역 조회
    List<TransactionSample> findWithdrawalsAfter(Moeim moeim, LocalDateTime transactionAt);

    // 모임 계좌의 거래 내역을 페이징하여 반환
    List<GetTransactionResponseDTO> getTransactionList(Long moeimId, int idx);

    // 특정 월의 거래 내역을 카테고리별 통계 정보로 변환하여 반환
    List<GetTransactionByCategoryResponseDTO> getTransactionListByCategory(Long moeimId, int year, int month);

    // 특정 기간 동안 특정 거래 유형의 거래 내역 조회
//    List<Transaction> findTransactionsByTypeWithinPeriod(String accountNumber, LocalDateTime start, LocalDateTime end, int transactionType);
}
