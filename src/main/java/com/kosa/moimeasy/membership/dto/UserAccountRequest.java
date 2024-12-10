package com.kosa.moimeasy.membership.dto;

import com.kosa.moimeasy.membership.entity.UserAccount;
import com.kosa.moimeasy.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class UserAccountRequest {

    private Long userId; // 회원 정보
    private String account_number; // 회원 계좌번호
    private String password; // 회원 계좌 비밀번호
    private BigDecimal balance; // 회원 계좌 잔액

    @Builder // 계좌 생성
    public UserAccountRequest(Long userId, String account_number, String password, BigDecimal balance) {
        this.userId = userId;
        this.account_number = account_number;
        this.password = password;
        this.balance = balance;
    }

    public UserAccount toEntity(User user) {
        return UserAccount.builder()
                .user(user)
                .accountNumber(account_number)
                .password(password)
                .balance(balance)
                .build();
    }
}
