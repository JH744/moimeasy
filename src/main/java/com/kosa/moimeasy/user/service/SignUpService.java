package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.user.dto.SignupDTO;
import com.kosa.moimeasy.user.exception.DuplicateEmailException;
import com.kosa.moimeasy.user.exception.DuplicateNicknameException;
import com.kosa.moimeasy.user.exception.SignupException;

public interface SignUpService {
    boolean signUp(SignupDTO dto) throws DuplicateEmailException, DuplicateNicknameException, SignupException;

    // 중복 검사
    boolean isEmailExists(String email);
    boolean isNicknameExists(String nickname);
}
