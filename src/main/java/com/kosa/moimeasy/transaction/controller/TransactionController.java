package com.kosa.moimeasy.transaction.controller;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.transaction.dto.*;
import com.kosa.moimeasy.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/transaction")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;

    // 회원 계좌에 입금
    @PutMapping("/userDeposit")
    public ResponseEntity<DepositDto.Response> userDeposit(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody @Valid DepositDto.Request request
    ) {
        log.info("token:{}, Received Request: {}",token, request.getUserId());
        return ResponseEntity.ok(transactionService.userDeposit(token, request));
    }

    // 모임 계좌에 입금
    @PutMapping("/moeimDeposit")
    public ResponseEntity<DepositDto.Response> moeimDeposit(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody @Valid DepositDto.Request request
    ) {
        log.info("token:{}, Received Request: {}",token, request.getUserId());
        return ResponseEntity.ok(transactionService.moeimDeposit(token, request));
    }

    // 출금
    @PutMapping("/withdraw")
    public ResponseEntity<WithdrawDto.Response> withdraw(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody @Valid WithdrawDto.Request request
    ) {
        log.info("token:{}, Received Request: {}",token, request);
        return ResponseEntity.ok(transactionService.withdraw(token, request));
    }

    // 송금
    @PutMapping("/remittance")
    public ResponseEntity<RemittanceDto.Response> remittance(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody @Valid RemittanceDto.Request request
    ) {
        log.info("token:{}, Received Request: {}",token, request);
        return ResponseEntity.ok(transactionService.remittance(token, request));
    }

    // 송금내역 조회
    @GetMapping("/remittance-list")
    public ResponseEntity<TransactionListDto.RemittanceListResponse> getRemittanceList(
            @RequestHeader(name = "Authorization") String token,
            @Valid TransactionListDto.Request request
    ) {
        log.info("token:{}, Received Request: {}",token, request);
        return ResponseEntity.ok(transactionService.getRemittanceList(token, request));
    }

    // 거래내역 조회
    @GetMapping("/transaction-list")
    public ResponseEntity<TransactionListDto.Response> getTransactionList(
            @RequestHeader(name = "Authorization") String token,
            @Valid TransactionListDto.Request request
    ) {
        log.info("token:{}, Received Request: {}",token, request);
        return ResponseEntity.ok(transactionService.getTransactionList(token, request));
    }

    // 송금내역 조회
    @GetMapping("/details")
    public ResponseEntity<DetailDTO> getDetails(
            @RequestHeader(name = "Authorization") String token
    ) {
        log.info("token:{}",token);
        return ResponseEntity.ok(transactionService.getDetials(token));
    }


}