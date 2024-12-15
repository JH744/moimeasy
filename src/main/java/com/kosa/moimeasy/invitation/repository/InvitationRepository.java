package com.kosa.moimeasy.invitation.repository;

import java.util.Optional;
import com.kosa.moimeasy.invitation.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findFirstByEmailOrderByCreatedAtDesc(String email);
}
