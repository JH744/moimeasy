package com.kosa.moimeasy.transaction.repository;

import com.kosa.moimeasy.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 계좌 id, 조회 기간을 정해서 가져오는 것
    @Query("SELECT t FROM Transaction t WHERE t.moeimAccount.moeimId = :moeimId AND t.transactedAt " +
            "BETWEEN :startDate AND :endDate ORDER BY t.transactedAt DESC")
    List<Transaction> findByMoeimAccountAndDateRange(Long moeimId, LocalDateTime startDate, LocalDateTime endDate
    );

}
