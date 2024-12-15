package com.kosa.moimeasy.user.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findByMoeimId(Long moeimId);

    Optional<User> findById(Long userId);

    Optional<User> findByAccountNumber(String accountNumber);
}
