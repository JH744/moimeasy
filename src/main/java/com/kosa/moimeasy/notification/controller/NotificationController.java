package com.kosa.moimeasy.notification.controller;

import com.kosa.moimeasy.notification.dto.NotificationDTO;
import com.kosa.moimeasy.notification.entity.Notification;
import com.kosa.moimeasy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 추가
    @PostMapping
    public NotificationDTO addNotification(@RequestBody NotificationDTO dto) {
        return notificationService.addNotification(dto);
    }

    // 유저의 모임 알림 조회
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMoeimNotifications(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getMoeimNotifications(userId));
    }

    // 알림 확인 시간 업데이트
    @PatchMapping("/update-viewed-time")
    public ResponseEntity<String> updateLastNotificationViewedAt(@RequestParam Long userId) {
        notificationService.updateLastNotificationViewedAt(userId);
        return ResponseEntity.ok("알림 확인 시간 업데이트 완료");
    }
}
