package com.kosa.moimeasy.membership.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class UserAccount extends BaseEntity { // 계좌

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_account_id")
    private Long id;

    // DB에는 object를 저장할 수 없다.
    // name 설정 시 JPA에서 User_ID로 매핑해준다.
    @ManyToOne(optional = false) // Account(Many) to User(One)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_account_number", nullable = false) // 길이 지정해야됌
    private String accountNumber;

    @Column(name = "user_account_password", nullable = false) // 유효성 검사 진행
    private String password;

    @Column(name = "user_account_balance", nullable = false)
    @ColumnDefault("0") // 잔액의 초기값을 0으로 설정
    private BigDecimal balance; // 오차 방지를 위해 Double 대신 BigDecimal 사용

    @OneToMany(mappedBy = "userAccount")
    private List<Fee> fee = new ArrayList<>();

    @Builder
    public UserAccount(Long id, User user, String accountNumber, String password, BigDecimal balance) {
        this.id = id;
        this.user = user;
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
    }
}
