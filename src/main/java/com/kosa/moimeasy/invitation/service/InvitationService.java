package com.kosa.moimeasy.invitation.service;

import com.kosa.moimeasy.invitation.entity.Invitation;
import com.kosa.moimeasy.invitation.entity.Invitation.InvitationStatus;
import com.kosa.moimeasy.invitation.dto.EmailRequest;
import com.kosa.moimeasy.invitation.repository.InvitationRepository;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

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

    public void sendInvitation(Long userId, EmailRequest request, String htmlContent) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }

        // 로그인된 사용자의 moeimId 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long moeimId = user.getMoeimId();

        if (moeimId == null) {
            throw new IllegalArgumentException("사용자의 moeimId가 null입니다.");
        }

        // moeimId로 모임 코드 조회
        String moeimCode = moeimRepository.findById(moeimId)
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."))
                .getMoeimCode();

        try {
            // 이메일 전송
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String finalHtmlContent = htmlContent + "<p>모임 코드: " + moeimCode + "</p>";
            helper.setTo(request.getEmail());
            helper.setSubject("MoeimEasy 초대");
            helper.setText(finalHtmlContent, true); // HTML 콘텐츠 설정
            helper.setFrom("moeimeasy@gmail.com", "MoeimEasy Team");

            mailSender.send(mimeMessage);

            // DB 저장
            Invitation invitation = new Invitation();
            invitation.setMoeimId(moeimId);
            invitation.setEmail(request.getEmail());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setCreatedAt(LocalDateTime.now());
            invitationRepository.save(invitation);

        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }

    public List<Invitation> getAllInvitations(Long userId) {
        // 사용자의 moeimId를 기반으로 초대 목록 필터링
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long moeimId = user.getMoeimId();

        return invitationRepository.findByMoeimId(moeimId);
    }
}

