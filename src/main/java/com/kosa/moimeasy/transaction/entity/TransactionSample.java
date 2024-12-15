package com.kosa.moimeasy.transaction.entity;

import com.kosa.moimeasy.common.entity.BaseEntity;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.transaction.dto.GetTransactionResponseDTO;
import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TransactionSample extends BaseEntity { // 거래내역

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // (회원 계좌와 거래내역은 1:N 관계)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // (모임 계좌와 거래내역은 1:N 관계)
    @JoinColumn(name = "moeimId", nullable = false)
    private Moeim moeim;

    @Column(name = "transfer_amount", nullable = false)
    private double transferAmount; //  이체 금액

    @Column(name = "deposit_amount", nullable = false)
    private double depositAmount; //  입금 금액

    @Column(name = "withdrawal_amount", nullable = false)
    private double withdrawalAmount; //  인출 금액

    @Column(name = "transaction_content", nullable = false)
    private String content; //  내용

    @Column(name = "transaction_type", nullable = false)
    private int transactionType; // 거래 유형 (출금 : 0, 입금 : 1)

    @Column(nullable = false)
    private String categoryName; // 소비항목




    // 모임 계좌에 입금 내역 생성 메서드
    public TransactionSample transaction(User userAccount, double amountAfterWithdrawal, String content, Moeim moeimAccount,
                                         double amountAfterDeposit, double transferAmount) {
        return TransactionSample.builder()
                .user(userAccount) // 유저와 연결된 계좌
                .moeim(moeimAccount) // 모임과 연결된 계좌
                .depositAmount(amountAfterDeposit) // 입금액
                .withdrawalAmount(amountAfterWithdrawal) // 출금액
                .transferAmount(transferAmount)
                .content(content) // 내용
                .transactionType(1) // 입금
                .build();
    }

    // 거래내역 생성 메서드
    public static TransactionSample createTransaction(User user, Moeim moeim, double depositAmount,
                                                      double withdrawalAmount, String content) {
        return TransactionSample.builder()
                .user(user) // 유저와 연결된 계좌
                .moeim(moeim) // 모임과 연결된 계좌
                .depositAmount(depositAmount) // 입금액
                .withdrawalAmount(withdrawalAmount) // 출금액
                .content(content) // 내용
                .transactionType(1) // 입금
                .build();
    }

    public GetTransactionResponseDTO toGetTransactionResponseDto(){
        return GetTransactionResponseDTO.builder()
                .content(content)
                .transactionType(transactionType)
                .transactionAmount((transactionType == 0 ? withdrawalAmount : depositAmount))
                .transactionAt(getCreateAt())
                .build();
    }

}
