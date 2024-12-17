package com.kosa.moimeasy.user.service;

import com.kosa.moimeasy.security.repository.MemberRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindServiceImpl implements FindService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public String findEmailByNicknameAndPhone(String nickname, String phone) throws UserNotFoundException {
        // 닉네임과 연락처로 회원 찾기
        User user =  memberRepository.findByNicknameAndPhone(nickname, phone)
                .orElseThrow(() -> new UserNotFoundException("일치하는 회원을 찾을 수 없습니다."));
        return user.getEmail();
    }
}
