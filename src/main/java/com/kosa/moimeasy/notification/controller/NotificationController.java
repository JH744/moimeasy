package com.kosa.moimeasy.notification.controller;

import com.kosa.moimeasy.notification.entity.NotificationEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final List<NotificationEntity> notifications = new ArrayList<>();

    @PostMapping
    public ResponseEntity<String> addNotification(@RequestBody NotificationEntity notification) {
        notifications.add(notification);
        return ResponseEntity.ok("알림 추가 완료");
    }

    @GetMapping("/recent")
    public List<NotificationEntity> getRecentNotifications() {
        // 최신 알림 5개 반환
        return notifications.stream()
                .sorted((n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp())) // 최신순 정렬
                .limit(5)
                .toList();
    }

    @GetMapping
    public List<NotificationEntity> getNotifications() {
        return notifications;
    }
}
