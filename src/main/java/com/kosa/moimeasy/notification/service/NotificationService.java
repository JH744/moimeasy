package com.kosa.moimeasy.notification.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.notification.dto.NotificationDTO;
import com.kosa.moimeasy.notification.entity.Notification;
import com.kosa.moimeasy.notification.repository.NotificationRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MoeimRepository moeimRepository;

    // 특정 사용자의 모임 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationDTO> getMoeimNotifications(Long userId) {
        // 유저 조회하기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 불가"));

        Long moeimId = user.getMoeimId(); // 유저가 속한 모임 ID
        LocalDateTime lastViewedAt = user.getLastNotificationViewedAt(); // 마지막 확인 시각

        // 알림 조회하기
        List<Notification> list  = notificationRepository.findNotificationsAfterLastViewedAt(moeimId, lastViewedAt);
        // DTO 변환 후 반환
        return list.stream()
                .map(NotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 알림 확인 시간 업데이트
    @Transactional
    public void updateLastNotificationViewedAt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 불가."));
        user.setLastNotificationViewedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 알림 추가
    public NotificationDTO addNotification(NotificationDTO dto) {
        // Moeim 객체 조회
        Moeim moeim = moeimRepository.findById(dto.getMoeimId())
                .orElseThrow(() -> new IllegalArgumentException("해당 모임 조회 불가"));
        // DTO -> 엔티티 변환
        Notification notification = dto.toEntity(moeim);
        // 알림 저장
        Notification savedNotification = notificationRepository.save(notification);
        // 저장된 알림을 DTO로 변환 후 반환
        return NotificationDTO.fromEntity(savedNotification);
    }
}
