package com.kosa.moimeasy.invitation.repository;

import com.kosa.moimeasy.invitation.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
