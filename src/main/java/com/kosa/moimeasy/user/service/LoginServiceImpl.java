package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.security.dto.AccessTokenDTO;
import com.kosa.moimeasy.user.dto.UserInfoDTO;
import com.kosa.moimeasy.security.dto.TokenResponseDTO;
import com.kosa.moimeasy.user.dto.LoginDTO;
import com.kosa.moimeasy.security.entity.RefreshTokenRedis;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.exception.LoginException;
import com.kosa.moimeasy.security.repository.MemberRepository;
import com.kosa.moimeasy.security.repository.RefreshTokenRepository;
import com.kosa.moimeasy.security.repository.RoleRepository;
import com.kosa.moimeasy.security.provider.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public LoginServiceImpl(AuthenticationManager authenticationManager,
                            JwtTokenProvider tokenProvider,
                            RefreshTokenRepository refreshTokenRepository,
                            MemberRepository memberRepository,
                            RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserInfoDTO userInfo(AccessTokenDTO accessToken) {
        Long userId = Long.valueOf(tokenProvider.getEmailFromJWT(accessToken.getAccessToken()));
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(userId);

        // Optional 처리
        Optional<User> optionalUser = memberRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Long roleId = Long.valueOf(user.getRole() != null ? user.getRole().getRoleId() : null);
            userInfoDTO.setRoleId(roleId);
        } else {
            throw new IllegalArgumentException("Member not found with id: " + userId);
        }

        return userInfoDTO;
    }

    @Override
    public TokenResponseDTO authenticate(LoginDTO loginDTO) throws LoginException {
        try {
            // DB에서 이메일 존재 여부 먼저 확인
            User user = memberRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new LoginException("가입되지 않은 아이디입니다."));
            // ↑ 여기서 이미 아이디가 존재하지 않으면 LoginException 발생

            // 비밀번호 인증 시도
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
            // ↑ 비밀번호가 틀린 경우 Spring Security 내부에서 BadCredentialsException 발생

            // 여기까지 도달했다면 인증 성공

            // Access Token 및 Refresh Token 생성
            String accessToken = tokenProvider.generateAccessToken(user.getUserId(), user.getRole().getRoleName());
            String refreshToken = tokenProvider.generateRefreshToken(user.getUserId());

            // Refresh Token 저장 (Redis, 사용자당 단일 토큰)
            RefreshTokenRedis tokenEntity = RefreshTokenRedis.builder()
                    .userId(user.getUserId())
                    .token(refreshToken)
                    .expiryDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration() / 1000))
                    .ttl(tokenProvider.getRefreshTokenExpiration() / 1000) // TTL을 초 단위로 설정
                    .build();
            refreshTokenRepository.save(tokenEntity); // 기존 토큰 덮어쓰기

            log.debug("Refresh Token 저장 성공: {}", refreshToken);

            return TokenResponseDTO.builder()
                    .userId(user.getUserId())
                    .moeimId(user.getMoeimId())
                    .profileImage(user.getProfileImage())
                    .name(user.getUserName())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .roleId(user.getRole().getRoleId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException ex) {
            // Spring Security 인증 시 비밀번호가 틀리면 발생
            log.error("로그인 실패(비밀번호 불일치): ", ex);
            throw new LoginException("비밀번호가 일치하지 않습니다.", ex);
        } catch (LoginException ex) {
            // 아이디가 존재하지 않는 경우 or 우리가 직접 던진 예외
            log.error("로그인 실패(아이디 존재x): ", ex);
            throw ex; // 메시지 "아이디가 일치하지 않습니다." 그대로 던짐
        } catch (Exception ex) {
            log.error("로그인 실패: ", ex);
            throw new LoginException("로그인에 실패했습니다.", ex);
        }
    }

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) throws LoginException {
        Long userId = tokenProvider.getUserIdFromJWT(refreshToken);
        Optional<RefreshTokenRedis> tokenOpt = refreshTokenRepository.findById(userId); // memberId 사용

        if (!tokenOpt.isPresent() || !tokenOpt.get().getToken().equals(refreshToken)
                || tokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new LoginException("유효하지 않은 Refresh Token입니다.");
        }

        User user = memberRepository.findById(userId)
                .orElseThrow(() -> new LoginException("사용자를 찾을 수 없습니다."));

        // 새로운 Access Token 생성
        String newAccessToken = tokenProvider.generateAccessToken(user.getUserId(), user.getRole().getRoleName());

        // 새로운 Refresh Token 생성
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteById(userId);

        // 새로운 Refresh Token 저장
        RefreshTokenRedis newTokenEntity = RefreshTokenRedis.builder()
                .userId(user.getUserId())
                .token(newRefreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration() / 1000))
                .ttl(tokenProvider.getRefreshTokenExpiration() / 1000) // TTL을 초 단위로 설정
                .build();
        refreshTokenRepository.save(newTokenEntity);

        log.debug("Refresh Token 갱신 성공: {}", newRefreshToken);

        return TokenResponseDTO.builder()
                .moeimId(user.getMoeimId())
                .userId(user.getUserId())
                .profileImage(user.getProfileImage())
                .name(user.getUserName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .roleId(user.getRole().getRoleId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        Long userId = tokenProvider.getUserIdFromJWT(refreshToken);
        refreshTokenRepository.deleteById(userId);
        log.debug("Logout 성공: memberId = {}", userId);
    }

    @Override
    public void saveRefreshToken(String refreshToken, Long userId) {
        RefreshTokenRedis tokenEntity = RefreshTokenRedis.builder()
                .userId(userId)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshTokenExpiration() / 1000))
                .ttl(tokenProvider.getRefreshTokenExpiration() / 1000) // TTL을 초 단위로 설정
                .build();
        refreshTokenRepository.save(tokenEntity);
        log.debug("Refresh Token 저장 성공: {}", refreshToken);
    }
}

