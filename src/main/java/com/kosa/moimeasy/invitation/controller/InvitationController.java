package com.kosa.moimeasy.invitation.controller;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/send")
    public ResponseEntity<?> sendInvitation(
            @RequestBody EmailRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        String moeimCode = invitationService.sendInvitation(userId, request, request.getHtmlContent());

        // 모임 코드와 성공 메시지를 함께 반환
        return ResponseEntity.ok(Map.of(
                "message", "초대가 성공적으로 전송되었습니다.",
                "moeimCode", moeimCode
        ));
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
