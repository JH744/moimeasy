package com.kosa.moimeasy.settlement.entity;

import com.kosa.moimeasy.chat.entity.BaseTimeEntity;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Table(name = "settlement")
@EntityListeners(AuditingEntityListener.class)
public class Settlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false)
    private SettlementStatus status = SettlementStatus.PENDING;

    public Settlement() {
    }

    public Settlement(String title, String imageUrl, User user, double amount) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.user = user;
        this.amount = amount;
        this.status = SettlementStatus.PENDING;
    }

    public enum SettlementStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}


