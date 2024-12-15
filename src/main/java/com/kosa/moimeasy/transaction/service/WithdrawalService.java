package com.kosa.moimeasy.transaction.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WithdrawalService {

    private MoeimRepository moeimRepository;
    private UserRepository userRepository;

    // 모임 계좌에서 출금
    @Transactional
    public void moeimAccountWithdraw(String moeimAccount, double amount){

        Moeim withdrawAccount = moeimRepository.findAccountNumber(moeimAccount);

        // 출금 후 계좌 잔액 업데이트
        double withdrawResult = withdrawAccount.getAmount() - amount;
        withdrawAccount.setAmount(withdrawResult);
    }

    // 회원 계좌에서 출금
//    @Transactional
//    public void userAccountWithdraw(Long userId, double amount){
//
//        User withdrawAccount = userRepository.findById(userId);
//
//        // 출금 후 계좌 잔액 업데이트
//        double withdrawResult = withdrawAccount.getAmount() - amount;
//        withdrawAccount.setAmount(withdrawResult);
//    }

}
