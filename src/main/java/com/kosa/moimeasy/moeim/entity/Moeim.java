package com.kosa.moimeasy.moeim.entity;

import java.time.LocalDateTime;

import com.kosa.moimeasy.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Moeim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moeimId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String moeimName;

    @Column(nullable = false, length = 50, unique = true)
    private String moeimCode;

    @Column(length = 50)
    private String accountNum;

    @Column(name = "CREATE_AT", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "UPDATE_AT")
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        this.moeimCode = generateRandomCode(); // 6자리 랜덤 코드 생성
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    private String generateRandomCode() {
        return String.format("%06d", (int) (Math.random() * 1000000)); // 6자리 랜덤 숫자
    }
}
