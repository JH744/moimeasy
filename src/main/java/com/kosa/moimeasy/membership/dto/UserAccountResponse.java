package com.kosa.moimeasy.membership.dto;

import com.kosa.moimeasy.membership.entity.UserAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class UserAccountResponse {

    private Long id;
    private String account_number; // 회원 계좌번호
    private String password; // 회원 계좌 비밀번호
    private BigDecimal balance; // 회원 계좌 잔액

    // Repository를 통해 조회한 entity를 dto로 변환하는 용도
    @Builder
    public UserAccountResponse(UserAccount userAccount) {
        this.id = userAccount.getId();
        this.account_number = userAccount.getAccountNumber();
        this.password = userAccount.getPassword();
        this.balance = userAccount.getBalance();
    }
}
