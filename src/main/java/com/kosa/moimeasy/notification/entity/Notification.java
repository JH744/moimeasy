package com.kosa.moimeasy.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notificationId;

    @Column(nullable = false)
    private String header; // 알림 제목

    @Column(nullable = false)
    private String body;  // 알림 본문

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="moeim_id")
    @JsonIgnore
    private Moeim moeim;

    public LocalDateTime getCreatedAt() {
       return super.getCreateAt();
    }

    public LocalDateTime getUpdatedAt() {
        return super.getUpdateAt();
    }
}

