package com.kosa.moimeasy.invitation.controller;

import java.util.List;

import com.kosa.moimeasy.invitation.service.InvitationService;
import com.kosa.moimeasy.invitation.dto.EmailRequest;
import com.kosa.moimeasy.invitation.entity.Invitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitations")
@CrossOrigin(origins = "http://localhost:3000")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping(value = "/send", consumes = "application/json", produces = "application/json")
    public String sendInvitation(@RequestBody EmailRequest request) {
        
        Long userId = 2L;
        invitationService.sendInvitation(userId, request, request.getHtmlContent());
        return "초대가 성공적으로 전송되었습니다.";
    }

    @GetMapping
    public List<Invitation> getInvitations() {
        return invitationService.getAllInvitations();
    }
}
