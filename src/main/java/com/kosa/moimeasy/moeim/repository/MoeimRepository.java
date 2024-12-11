package com.kosa.moimeasy.moeim.repository;

import java.util.Optional;
import com.kosa.moimeasy.moeim.entity.Moeim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MoeimRepository extends JpaRepository<Moeim, Long> {

    Optional<Moeim> findById(Long moeimId);

    // 입력한 모임 id를 모임 테이블에서 조회
    @Query("select m from Moeim m where m.moeimId = :moeimId")
    Optional<Moeim> findAccountNumberById(@Param("moeimId") Long moeimId);

    // 입력한 계좌를 모임 테이블에서 조회
    @Query("select m from Moeim m where m.accountNumber = :accountNumber")
    Moeim findAccountNumber(@Param("accountNumber") String accountNumber);

}
