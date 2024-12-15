package com.kosa.moimeasy.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntity {
    private String severity;
    private String summary;
    private String detail;
    private long timestamp; // 알림 시간
}

