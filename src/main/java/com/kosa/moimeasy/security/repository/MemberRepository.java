package com.kosa.moimeasy.security.repository;


import com.kosa.moimeasy.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT m FROM User m JOIN FETCH m.role WHERE m.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    User findByEmailAndPassword(String email, String password);

    List<User> findByEmailContainingIgnoreCase(String email);
    boolean existsByEmailContaining(String email);

    Optional<User> findByNicknameAndPhone(String nickname, String phone);

    Optional<User> findByNicknameAndPhoneAndEmail(String nickname, String phone, String email);
}
