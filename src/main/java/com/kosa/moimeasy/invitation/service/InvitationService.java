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

    public String sendInvitation(Long userId, EmailRequest request) {
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
            // HTML 이메일 콘텐츠 생성
            String htmlContent = String.format("""
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>MoeimEasy 초대장</title>
        </head>
        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f7f7f7; color: #333;">
            <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center;">
                <div style="background-color: #7f56d9; color: #fff; padding: 20px;">
                    <h1 style="margin: 0; font-size: 24px;">MoeimEasy에서 초대합니다!</h1>
                </div>
                <div style="padding: 20px;">
                    <p style="font-size: 16px; line-height: 1.5; color: #414651;">%s</p>
                    <p style="font-size: 18px; font-weight: bold; color: #7f56d9;">모임 코드: %s</p>
                </div>
                <div style="padding: 10px; background-color: #f1f1f1; font-size: 12px; color: #888;">
                    <p style="margin: 0;">이 이메일은 MoeimEasy의 초대 이메일입니다. 수신을 원하지 않는다면 무시하세요.</p>
                </div>
            </div>
        </body>
        </html>
        """, request.getMessage(), moeimCode);

            // 이메일 전송
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(request.getEmail());
            helper.setSubject("MoeimEasy 초대");
            helper.setText(htmlContent, true);
            helper.setFrom("moeimeasy@gmail.com", "MoeimEasy Team");

            mailSender.send(mimeMessage);

            // DB 저장
            Invitation invitation = new Invitation();
            invitation.setMoeimId(moeimId);
            invitation.setEmail(request.getEmail());
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setCreatedAt(LocalDateTime.now());
            invitationRepository.save(invitation);

            return moeimCode;

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
