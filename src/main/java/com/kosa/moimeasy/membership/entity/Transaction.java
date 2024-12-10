package com.kosa.moimeasy.membership.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.mapping.ToOne;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(optional = false) // (모임 계좌와 거래내역은 N:1 관계)
    @JoinColumn(name = "moiem_account_id", nullable = false)
    private MoeimAccount moeimAccount;

    @OneToOne // 회비와 거래내역은 1대1 매핑
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;

    @Column(name = "transaction_money", nullable = false)
    private Double money;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private enum TransactionType{
        income, // 수입
        expense // 지출
    }
}
