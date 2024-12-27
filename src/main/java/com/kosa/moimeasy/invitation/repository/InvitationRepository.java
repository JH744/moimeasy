package com.kosa.moimeasy.invitation.repository;

import java.util.List;
import java.util.Optional;
import com.kosa.moimeasy.invitation.entity.Invitation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;


@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findFirstByEmailOrderByCreatedAtDesc(String email);

    @Query("SELECT i FROM Invitation i WHERE i.moeimId = :moeimId")
    List<Invitation> findByMoeimId(@Param("moeimId") Long moeimId);
}
