package com.kosa.moimeasy.invitation.service;

import com.kosa.moimeasy.invitation.entity.Invitation;
import com.kosa.moimeasy.invitation.entity.Invitation.InvitationStatus;
import com.kosa.moimeasy.invitation.dto.EmailRequest;
import com.kosa.moimeasy.invitation.repository.InvitationRepository;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
public class InvitationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MoeimRepository moeimRepository;

    public String sendInvitation(Long userId, EmailRequest request) {
        try {
            // 로그인된 회원의 moeimId 및 moeimCode 가져오기
            Long moeimId = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                    .getMoeimId();

            Moeim moeim = moeimRepository.findById(moeimId)
                    .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

            String moeimCode = moeim.getMoeimCode();

            // 이메일 전송
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(request.getEmail());
            helper.setSubject("Moeimeasy 초대");
            helper.setText(request.getMessage() + "\n\n모임 코드: " + moeimCode, true); // HTML 허용
            helper.setFrom("moeimeasy@gmail.com", "MoeimEasy Team");

            mailSender.send(mimeMessage);

            // DB 저장
            Invitation invitation = new Invitation();
            invitation.setMoeimId(moeimId);
            invitation.setEmail(request.getEmail());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setCreatedAt(LocalDateTime.now());
            invitationRepository.save(invitation);

            return "초대장이 성공적으로 전송되었습니다!";
        } catch (Exception e) {
            e.printStackTrace();
            return "이메일 전송 실패: " + e.getMessage();
        }
    }
}
