package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.security.dto.AccessTokenDTO;
import com.kosa.moimeasy.user.dto.LoginDTO;
import com.kosa.moimeasy.user.dto.UserInfoDTO;
import com.kosa.moimeasy.security.dto.TokenResponseDTO;
import com.kosa.moimeasy.user.exception.LoginException;

public interface LoginService {
    TokenResponseDTO authenticate(LoginDTO loginDTO) throws LoginException;
    TokenResponseDTO refreshToken(String refreshToken) throws LoginException;
    void logout(String refreshToken);
    void saveRefreshToken(String refreshToken, Long userId);

    UserInfoDTO userInfo(AccessTokenDTO accessToken);
}
