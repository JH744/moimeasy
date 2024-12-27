package com.kosa.moimeasy.transaction.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.transaction.type.TransactionType;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // (모임 계좌와 거래내역은 1:N 관계)
    @JoinColumn(name = "moeim_Id")
    private Moeim moeimAccount; // 모임 계좌

    @ManyToOne(fetch = FetchType.LAZY) // (회원 계좌와 거래내역은 1:N 관계)
    @JoinColumn(name = "user_Id")
    private User userAccount; // 유저 계좌

    @NotNull
    private double amount; // 이체 금액

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // 거래 유형(입금, 출금, 이체)

    private String depositName; // 입금자명

    private String withdrawName; // 출금자명

    private String receivedName; // 송금받는 계좌명

    private String receivedAccount; // 송금받는 계좌번호

    private String categoryName; // 소비항목

    @CreatedDate
    private LocalDateTime transactedAt; // 거래 시간

}
