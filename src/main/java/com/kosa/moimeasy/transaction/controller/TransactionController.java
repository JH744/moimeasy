package com.kosa.moimeasy.transaction.controller;

import com.kosa.moimeasy.transaction.dto.*;
import com.kosa.moimeasy.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "거래내역 페이지", description = "Transaction API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/transaction")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;

    // 회원 계좌에 입금
    @Operation(summary = "회원 계좌 입금", description = "회원의 계좌에 돈을 입금하는 API")
    @ApiResponse(responseCode = "200", description = "계좌 입금 성공")
    @ApiResponse(responseCode = "500", description = "비밀번호 틀린 경우")
    @PutMapping("/userDeposit")
    public ResponseEntity<DepositDto.Response> userDeposit( @RequestBody @Valid DepositDto.Request request) {
        return ResponseEntity.ok(transactionService.userDeposit(request));
    }

    // 모임 계좌에 입금
    @Operation(summary = "모임 계좌 입금", description = "모임의 계좌에 돈을 입금하는 API")
    @ApiResponse(responseCode = "200", description = "계좌 입금 성공")
    @ApiResponse(responseCode = "401", description = "모임에 가입되지 않은 경우")
    @PutMapping("/moeimDeposit")
    public ResponseEntity<DepositDto.Response> moeimDeposit(
            @RequestBody @Valid DepositDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.moeimDeposit(request));
    }

    // 출금
    @Operation(summary = "모임 계좌 출금", description = "모임의 계좌의 돈을 사용하는 API")
    @ApiResponse(responseCode = "200", description = "계좌 출금 성공")
    @ApiResponse(responseCode = "401", description = "계좌의 돈이 부족한 경우")
    @PutMapping("/withdraw")
    public ResponseEntity<WithdrawDto.Response> withdraw(
            @RequestBody @Valid WithdrawDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    // 송금을 위한 회원 조회
    @Operation(summary = "회원 조회", description = "송금을 위한 회원 정보 조회 API")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "모임에 가입되지 않은 경우")
    @GetMapping("/details")
    public ResponseEntity<DetailDto> getDetails(
            @RequestParam Long userId,
            @RequestParam Long moeimId
    ) {
        return ResponseEntity.ok(transactionService.getDetials(userId, moeimId));
    }
    
    // 송금
    @Operation(summary = "계좌 송금", description = "회원 계좌에서 모임 계좌로 돈을 송금하는 API")
    @ApiResponse(responseCode = "200", description = "송금 성공")
    @ApiResponse(responseCode = "500", description = "회원 계좌의 돈이 부족한 경우, 회비를 이미 납부한 경우")
    @PutMapping("/remittance")
    public ResponseEntity<RemittanceDto.Response> remittance(
            @RequestBody @Valid RemittanceDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.remittance(request));
    }

    // 송금내역 조회
    @Operation(summary = "회비 납부 리스트", description = "회원들의 모임 회비 납부 리스트 조회 API")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "모임에 가입되지 않은 경우")
    @GetMapping("/remittance-list")
    public ResponseEntity<TransactionListDto.RemittanceListResponse> getRemittanceList(
            @Valid TransactionListDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.getRemittanceList(request));
    }

    // 거래내역 조회
    @Operation(summary = "거래내역 조회 리스트", description = "모임 계좌의 거래내역 조회 API")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "모임에 가입되지 않은 경우")
    @GetMapping("/transaction-list")
    public ResponseEntity<TransactionListDto.Response> getTransactionList(
            @Valid TransactionListDto.Request request
    ) {
        return ResponseEntity.ok(transactionService.getTransactionList(request));
    }
    
    // 카테고리를 위한 기본 조회
    @Operation(summary = "카테고리 내역 조회", description = "모임 계좌 카테고리 정보 조회 API")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "모임에 가입되지 않은 경우")
    @GetMapping("/category")
    public ResponseEntity<InitialDataDto> getTransactionDetails(
            InitialDataDto request
    ) {
        return ResponseEntity.ok(transactionService.getInitialData(request));
    }

    // 입력한 비밀번호가 맞는지 확인
    @Operation(summary = "비밀번호 검증", description = "비밀번호 검증 API")
    @ApiResponse(responseCode = "200", description = "검증 성공")
    @ApiResponse(responseCode = "401", description = "비밀번호 오류인 경우")
    @PostMapping("/password")
    public ResponseEntity<PasswordCheckDto.Response> checkPassword(
    @Valid @RequestBody PasswordCheckDto.Request passwordCheckDto) {
            return ResponseEntity.ok(transactionService.checkPassword(passwordCheckDto));
    }
}