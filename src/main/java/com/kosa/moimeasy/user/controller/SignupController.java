package com.kosa.moimeasy.user.controller;

import com.kosa.moimeasy.user.dto.SignupDTO;
import com.kosa.moimeasy.user.exception.DuplicateEmailException;
import com.kosa.moimeasy.user.exception.DuplicateNicknameException;
import com.kosa.moimeasy.user.exception.SignupException;
import com.kosa.moimeasy.user.service.SignUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/signup")
public class SignupController {

    private final SignUpService signUpService;

    @Autowired
    public SignupController(SignUpService signUpService) {

        this.signUpService = signUpService;
    }

    // 회원가입 요청
    @PostMapping
    public ResponseEntity signup(@Valid @RequestBody SignupDTO dto, BindingResult bindingResult) {
        log.info("Request Data: {}", dto);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            boolean success = signUpService.signUp(dto);
            Map<String, String> response = new HashMap<>();
            if (success) {

                response.put("message", "회원가입이 성공적으로 완료되었습니다.");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("error", "회원가입에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

            }
        } catch (DuplicateEmailException | DuplicateNicknameException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (SignupException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        boolean exists = signUpService.isEmailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // 닉네임 중복 검사
    @GetMapping("/check-nickname")
    public ResponseEntity<?>
    checkNickname(@RequestParam("nickname") String nickname) {
        boolean exists = signUpService.isNicknameExists(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}