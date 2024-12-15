package com.kosa.moimeasy.transaction.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositService {

    private MoeimRepository moeimRepository;

    // 모임 계좌에 입금
    @Transactional
    public void deposit(String depositAccountNumber, double amount){ // 입금할 계좌번호, 입금 금액

        // 모임 계좌 조회
        Moeim depositAccount = moeimRepository.findAccountNumber(depositAccountNumber);

        // 입금할 금액을 모임의 잔액에 저장
        double depositResult = depositAccount.getAmount() + amount;
        depositAccount.setAmount(depositResult);
    }
}
