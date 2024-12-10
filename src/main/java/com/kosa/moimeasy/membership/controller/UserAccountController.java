package com.kosa.moimeasy.membership.controller;

import com.kosa.moimeasy.membership.dto.UserAccountRequest;
import com.kosa.moimeasy.membership.dto.UserAccountResponse;
import com.kosa.moimeasy.membership.entity.UserAccount;
import com.kosa.moimeasy.membership.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/membership")
@CrossOrigin(origins = "http://localhost:3000") // Axis 요청
public class UserAccountController {

    private final UserAccountService userAccountService;

    // 회원 계좌 생성
    @PostMapping()
    public ResponseEntity<UserAccount> createUserAccount(@RequestBody UserAccountRequest requestDTO) {
        userAccountService.createUserAccount(requestDTO);
        return new ResponseEntity<>(HttpStatus.CREATED); // 성공적으로 생성된 경우
    }

    // 전체 회원 계좌 조회
    @GetMapping
    public ResponseEntity<List<UserAccountResponse>> getUserAccounts() {
        List<UserAccountResponse> userAccountList = userAccountService.getAllUserAccounts();
        return ResponseEntity.ok(userAccountList);
    }
}
