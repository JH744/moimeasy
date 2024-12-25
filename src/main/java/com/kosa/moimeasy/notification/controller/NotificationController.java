package com.kosa.moimeasy.notification.controller;

import com.kosa.moimeasy.notification.dto.NotificationDTO;
import com.kosa.moimeasy.notification.entity.Notification;
import com.kosa.moimeasy.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Tag(name="모임 알림" , description="Notifications API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 알림 추가
    @Operation(summary = "알림 추가" ,description ="알림 추가하기" )
    @PostMapping
    public NotificationDTO addNotification(@RequestBody NotificationDTO dto) {
        return notificationService.addNotification(dto);
    }

    // 유저의 모임 알림 조회
    @Operation(summary = "알림 조회" ,description ="알림 조회하기" )
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMoeimNotifications(@RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getMoeimNotifications(userId));
    }

    // 알림 확인 시간 업데이트
    @Operation(summary = "유저 알림 확인 시각 업데이트" ,description ="유저 알림 시각 업데이트하기" )
    @PatchMapping("/update-viewed-time")
    public ResponseEntity<String> updateLastNotificationViewedAt(@RequestParam Long userId) {
        notificationService.updateLastNotificationViewedAt(userId);
        return ResponseEntity.ok("알림 확인 시간 업데이트 완료");
    }
}
