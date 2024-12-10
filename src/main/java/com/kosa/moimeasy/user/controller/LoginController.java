package com.kosa.moimeasy.user.controller;

import com.kosa.moimeasy.security.dto.TokenResponseDTO;
import com.kosa.moimeasy.security.provider.JwtTokenProvider;
import com.kosa.moimeasy.user.dto.LoginDTO;
import com.kosa.moimeasy.user.service.LoginService;
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
                    .sameSite("Strict")
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());

            // Access Token은 JSON 응답으로 전달
            TokenResponseDTO responseBody = TokenResponseDTO.builder()
                    .userId(tokenResponseDTO.getUserId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .name(tokenResponseDTO.getName()) //dto, entity db설정 후 수정 해야할수도 있음
                    .email(tokenResponseDTO.getEmail()) //dto, entity db설정 후 수정 해야할수도 있음
                    .nickname(tokenResponseDTO.getNickname()) //dto, entity db설정 후 수정 해야할수도 있음
                    .roleId(tokenResponseDTO.getRoleId()) //dto, entity db설정 후 수정 해야할수도 있음
                    .accessToken(tokenResponseDTO.getAccessToken()) //dto, entity db설정 후 수정 해야할수도 있음
                    .build();

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("로그인 실패: ", e); //예외의 스택 트레이스를 함께 로그에 남김
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인에 실패했습니다.");

        }
    }

    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null && !refreshToken.isEmpty()) {
            loginService.logout(refreshToken);

            // Refresh Token 쿠키 삭제 (SameSite 속성 포함)
            ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(true) // HTTPS 사용 시 true
                    .path("/api/v1/refresh-token")
                    .maxAge(0)
                    .sameSite("Strict")
                    .build();
            response.addHeader("Set-Cookie", deleteCookie.toString());

            return ResponseEntity.ok().body("로그아웃 되었습니다.");
        }
        return ResponseEntity.badRequest().body("Refresh Token이 필요합니다.");
    }

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




