package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.user.exception.UserNotFoundException;

public interface FindService {
    String findEmailByNicknameAndPhone(String nickname, String phone) throws UserNotFoundException;
}
