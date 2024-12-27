package com.kosa.moimeasy.transaction.controller;

import com.kosa.moimeasy.transaction.dto.*;
import com.kosa.moimeasy.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/transaction")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;

    // 회원 계좌에 입금
    @PutMapping("/userDeposit")
    public ResponseEntity<DepositDto.Response> userDeposit( @RequestBody @Valid DepositDto.Request request) {
        return ResponseEntity.ok(transactionService.userDeposit(request));
    }

    // 모임 계좌에 입금
    @PutMapping("/moeimDeposit")
    public ResponseEntity<DepositDto.Response> moeimDeposit(
            @RequestBody @Valid DepositDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.moeimDeposit(request));
    }

    // 출금
    @PutMapping("/withdraw")
    public ResponseEntity<WithdrawDto.Response> withdraw(
            @RequestBody @Valid WithdrawDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    // 송금
    @PutMapping("/remittance")
    public ResponseEntity<RemittanceDto.Response> remittance(
            @RequestBody @Valid RemittanceDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.remittance(request));
    }

    // 송금내역 조회
    @GetMapping("/remittance-list")
    public ResponseEntity<TransactionListDto.RemittanceListResponse> getRemittanceList(
            @Valid TransactionListDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.getRemittanceList(request));
    }

    // 거래내역 조회
    @GetMapping("/transaction-list")
    public ResponseEntity<TransactionListDto.Response> getTransactionList(
            @Valid TransactionListDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.getTransactionList(request));
    }

    // 송금을 위한 기본 조회
    @GetMapping("/details")
    public ResponseEntity<DetailDto> getDetails(
            @RequestParam Long userId,
            @RequestParam Long moeimId
    ) {
        return ResponseEntity.ok(transactionService.getDetials(userId, moeimId));
    }

    // 카테고리를 위한 기본 조회
    @GetMapping("/category")
    public ResponseEntity<InitialDataDto> getTransactionDetails(
            InitialDataDto request
    ) {
        return ResponseEntity.ok(transactionService.getInitialData(request));
    }
}