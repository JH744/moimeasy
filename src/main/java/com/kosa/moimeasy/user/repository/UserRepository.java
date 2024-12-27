package com.kosa.moimeasy.user.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.moeimId = :moeimId")
    List<User> findByMoeimId(@Param("moeimId") Long moeimId);

    Optional<User> findById(Long userId);

    Optional<User> findByAccountNumber(String accountNumber);
}
