package com.kosa.moimeasy.moeim.repository;

import java.util.Optional;
import com.kosa.moimeasy.moeim.entity.Moeim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MoeimRepository extends JpaRepository<Moeim, Long> {

    Optional<Moeim> findById(Long moeimId);

    // 입력한 모임 id를 모임 테이블에서 조회
    @Query("select m from Moeim m where m.moeimId = :moeimId")
    Optional<Moeim> findAccountNumberById(@Param("moeimId") Long moeimId);

    // 입력한 계좌를 모임 테이블에서 조회
    @Query("select m from Moeim m where m.accountNumber = :accountNumber")
    Moeim findAccountNumber(@Param("accountNumber") String accountNumber);

    Optional<Moeim> findByAccountNumber(String accountNumber);

    // 유저 아이디로 모임 조회
    @Query("SELECT m FROM Moeim m WHERE m.user.userId = :userId")
    Optional<Moeim> findByUserId(@Param("userId") Long userId);
}
