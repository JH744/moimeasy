package com.kosa.moimeasy.invitation.controller;

import java.util.List;
import java.util.Map;

import com.kosa.moimeasy.invitation.service.InvitationService;
import com.kosa.moimeasy.invitation.dto.EmailRequest;
import com.kosa.moimeasy.invitation.entity.Invitation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "모임 초대페이지", description = "Moeim Invitation API")
@RestController
@RequestMapping("/api/v1/invitations")
@CrossOrigin(origins = "http://192.168.5.49:3000")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @Operation(summary = "모임 초대", description = "모임에 초합니다.")
    @ApiResponse(responseCode = "200", description = "모임 초대 성공")
    @PostMapping("/send")
    public ResponseEntity<?> sendInvitation(
            @RequestBody EmailRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        String moeimCode = invitationService.sendInvitation(userId, request);

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
