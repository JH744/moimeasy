package com.kosa.moimeasy.invitation.controller;

import java.util.List;

import com.kosa.moimeasy.invitation.service.InvitationService;
import com.kosa.moimeasy.invitation.dto.EmailRequest;
import com.kosa.moimeasy.invitation.entity.Invitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitations")
@CrossOrigin(origins = "http://localhost:3000")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping(value = "/send", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> sendInvitation(
            @RequestBody EmailRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
        }

        Long userId = Long.valueOf(userDetails.getUsername()); // 현재 로그인된 사용자 ID

        try {
            invitationService.sendInvitation(userId, request, request.getHtmlContent());
            return ResponseEntity.ok("초대가 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("초대 전송 실패: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Invitation>> getInvitations(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        List<Invitation> invitations = invitationService.getAllInvitations(userId);
        return ResponseEntity.ok(invitations);
    }
}
