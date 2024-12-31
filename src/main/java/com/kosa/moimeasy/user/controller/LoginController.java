package com.kosa.moimeasy.user.controller;

import com.kosa.moimeasy.security.dto.TokenResponseDTO;
import com.kosa.moimeasy.security.provider.JwtTokenProvider;
import com.kosa.moimeasy.user.dto.LoginDTO;
import com.kosa.moimeasy.user.exception.LoginException;
import com.kosa.moimeasy.user.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="로그인 페이지" , description="Login API")
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private final LoginService loginService;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public LoginController(LoginService loginService, JwtTokenProvider tokenProvider) {

        this.loginService = loginService;
        this.tokenProvider = tokenProvider;
    }
    @Operation(summary = "로그인 접속" ,description ="로그인" )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        try{
            TokenResponseDTO tokenResponseDTO = loginService.authenticate(loginDTO);

            // Refresh Token을 HttpOnly 쿠키에 설정 (SameSite 속성 포함)
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenResponseDTO.getRefreshToken())
                    .httpOnly(true)
                    .secure(false) // 개발 환경에서는 false, 배포 시 true
                    .path("/api/v1/refresh-token")
                    .maxAge(tokenProvider.getRefreshTokenExpiration() / 1000)
                    .sameSite("None") // <-- "Strict" 대신 "None"
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());

            // Access Token은 JSON 응답으로 전달
            TokenResponseDTO responseBody = TokenResponseDTO.builder()
                    .userId(tokenResponseDTO.getUserId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .moeimId(tokenResponseDTO.getMoeimId())//dto, entity db설정 후 수정 해야할수도 있음
                    .profileImage(tokenResponseDTO.getProfileImage()) //dto, entity db설정 후 수정 해야할수도 있음
                    .name(tokenResponseDTO.getName()) //dto, entity db설정 후 수정 해야할수도 있음
                    .email(tokenResponseDTO.getEmail()) //dto, entity db설정 후 수정 해야할수도 있음
                    .nickname(tokenResponseDTO.getNickname()) //dto, entity db설정 후 수정 해야할수도 있음
                    .roleId(tokenResponseDTO.getRoleId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .accessToken(tokenResponseDTO.getAccessToken()) //dto, entity db설정 후 수정 해야할수도 있음
                    .build();

            return ResponseEntity.ok(responseBody);
        } catch (LoginException e) {
            // 백엔드에서 던진 커스텀 예외(LoginException)라면, 그 메시지를 그대로 내려줌
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // 그 외 예외
            log.error("로그인 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 오류가 발생했습니다.");
        }
    }
    @Operation(summary = "로그아웃" ,description ="로그아웃" )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Refresh Token 쿠키 확인
            String refreshToken = null;
            if (request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("refresh_token".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest().body("Refresh Token이 필요합니다.");
            }

            // Refresh Token 로그아웃 처리
            loginService.logout(refreshToken);

            // Refresh Token 쿠키 삭제
            ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(false) // HTTPS 환경에서는 true로 변경
                    .path("/api/v1/refresh-token")
                    .maxAge(0)// 즉시 만료
                    .sameSite("Strict") // None으로 설정하여 모든 요청에 쿠키 포함  수정 전 :Strict
                    .build();
            response.addHeader("Set-Cookie", deleteCookie.toString());

            return ResponseEntity.ok("로그아웃 성공");
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 중 오류가 발생했습니다.");
        }
    }
    @Operation(summary = "리플래쉬&엑세스 토큰" ,description ="리플래쉬&엑세스 토큰 생성&삭제" )
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh Token이 필요합니다.");
        }

        try {
            TokenResponseDTO tokenResponseDTO = loginService.refreshToken(refreshToken);

            // 새로운 Refresh Token 생성 및 업데이트
            String newRefreshToken = tokenProvider.generateRefreshToken(tokenResponseDTO.getUserId());
            loginService.logout(refreshToken); // 기존 Refresh Token 삭제
            loginService.saveRefreshToken(newRefreshToken,tokenResponseDTO.getUserId()); // 새로운 Refresh Token 저장 //email 수정 할수도 있음

            // 새로운 Refresh Token을 HttpOnly 쿠키에 설정 (SameSite 속성 포함)
            ResponseCookie newRefreshCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                    .httpOnly(true)
                    .secure(false) // HTTPS 사용 시 true, 현재는 HTTP 사용
                    .path("/api/v1/refresh-token")
                    .maxAge(tokenProvider.getRefreshTokenExpiration() / 1000)
                    .sameSite("Strict")
                    .build();
            response.addHeader("Set-Cookie", newRefreshCookie.toString());

            // Access Token은 JSON 응답으로 전달
            TokenResponseDTO responseBody = TokenResponseDTO.builder()
                    .userId(tokenResponseDTO.getUserId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .moeimId(tokenResponseDTO.getMoeimId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .name(tokenResponseDTO.getName()) //dto, entity db설정 후 수정 해야할수도 있음
                    .email(tokenResponseDTO.getEmail()) //dto, entity db설정 후 수정 해야할수도 있음
                    .nickname(tokenResponseDTO.getNickname()) //dto, entity db설정 후 수정 해야할수도 있음
                    .roleId(tokenResponseDTO.getRoleId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .accessToken(tokenResponseDTO.getAccessToken()) //dto, entity db설정 후 수정 해야할수도 있음
                    .build();

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("토큰 갱신 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}




