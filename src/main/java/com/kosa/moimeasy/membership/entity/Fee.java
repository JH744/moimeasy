package com.kosa.moimeasy.membership.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Fee extends BaseEntity { // 회비

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

//    @Enumerated(EnumType.STRING) 회원 타입 User에 추가
//    @Column(name = "fee_type", nullable = false)
//    private feeType type;

    @Column(name = "fee_money", nullable = false)
    private BigDecimal money;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_status", nullable = false)
    private FeeStatus status;

    @Column(name = "fee_memo")
    private String memo;

    public enum FeeStatus {
        PAID, // 납부완료
        UNPAID, // 회비미납
    }
}
