package com.kosa.moimeasy.user.entity;

import com.kosa.moimeasy.transaction.entity.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY) // Role과의 관계 설정
    @JoinColumn(name = "role_id", nullable = false) // 외래 키 매핑
    private Role role;

    @Column(length = 25)
    private String nickname;

    @Column
    private Long moeimId;

    @Column(name = "user_account_number", nullable = false) // 길이 지정해야됌
    private String accountNumber;

//    @Column(name = "user_account_password", nullable = false) // 유효성 검사 진행
//    private String accountPassword;

    @Column(name = "user_account_balance", nullable = false)
    private double balance;

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        if(this.createAt == null){
            this.createAt = LocalDateTime.now();
        }else {
            this.createAt = createAt;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

    // 거래내역 테이블
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Transaction> transaction = new ArrayList<>();

    // 유저 계좌에 입금
    public void deposit(double balance){
        this.balance += balance;
    }

    // 유저 계좌에서 출금
    public void withdraw(double balance){
        if(this.balance< balance){
            throw new IllegalArgumentException("잔액 부족");
        }
        this.balance -= balance;
    }
}



