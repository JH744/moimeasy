package com.kosa.moimeasy.membership.service;

import com.kosa.moimeasy.membership.dto.UserAccountRequest;
import com.kosa.moimeasy.membership.dto.UserAccountResponse;
import com.kosa.moimeasy.membership.entity.UserAccount;
import com.kosa.moimeasy.membership.repository.UserAccountRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final UserRepository userRepository;

    @Transactional // 계좌 생성
    public void createUserAccount(UserAccountRequest requestDTO){
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("유저 정보 없음")); // User가 존재하지 않을 때 예외 처리

        UserAccount userAccount = requestDTO.toEntity(user);
        userAccountRepository.save(userAccount);
    }

    // 생성된 계좌 리스트 반환
    @Transactional
    public List<UserAccountResponse> getAllUserAccounts(){
        List<UserAccount> userAccounts = userAccountRepository.findAll();

        return userAccounts.stream()
                .map(UserAccountResponse::new)
                .collect(Collectors.toList());
    }
}
