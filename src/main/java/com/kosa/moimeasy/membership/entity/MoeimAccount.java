package com.kosa.moimeasy.membership.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class MoeimAccount extends BaseEntity { // 모임 계좌

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "account_id")
        private Long id;

        // DB에는 object를 저장할 수 없다.
        // name 설정 시 JPA에서 User_ID로 매핑해준다.
        @ManyToOne // Account(Many) to User(One)
        @JoinColumn(name = "moeim_id", nullable = false)
        private Moeim moeim;

        @Column(name = "moeim_account_number", nullable = false) // 길이 지정해야됌
        private String accountNumber;

        @Column(name = "moeim_account_password", nullable = false) // 유효성 검사 진행
        private String password;

        @Column(name = "moeim_account_balance", nullable = false)
        private BigDecimal balance; // 오차 방지를 위해 Double 대신 BigDecimal 사용

        @OneToMany(mappedBy = "moeimAccount")
        private List<Transaction> transactions;
}
