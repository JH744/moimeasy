package com.kosa.moimeasy.notification.dto;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.notification.entity.Notification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private long notificationId;
    private String header; // 알림 제목
    private String body;  // 알림 본문
    private long moeimId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;



    // DTO -> Entity 변환 메서드
    public Notification toEntity(Moeim moeim) {
        return Notification.builder()
                .notificationId(this.notificationId) // 이미 존재하는 ID (생략 가능)
                .header(this.header)
                .body(this.body)
                .moeim(moeim) // Moeim 엔티티 필요
                .build();
    }

    // fromEntity 메서드: Notification 엔티티 -> DTO 변환
    public static NotificationDTO fromEntity(Notification notification) {
        return new NotificationDTO(
                notification.getNotificationId(),
                notification.getHeader(),
                notification.getBody(),
                notification.getMoeim().getMoeimId(), // moeimId
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
