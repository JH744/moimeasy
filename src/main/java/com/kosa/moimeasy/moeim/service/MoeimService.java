package com.kosa.moimeasy.moeim.service;

import com.kosa.moimeasy.moeim.dto.MoeimDTO;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosa.moimeasy.user.repository.UserRepository;

import java.util.Random;

@Service
public class MoeimService {

    @Autowired
    private MoeimRepository moeimRepository;

    @Autowired
    private UserRepository userRepository;

    public Moeim createMoeim(MoeimDTO request) {
        Moeim moeim = new Moeim();
        
        // 모임 이름 설정
        moeim.setMoeimName(request.getMoeimName());

        // 사용자 조회 후 설정
        moeim.setUser(userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        // 가상 계좌 번호 자동 생성 (10자리 숫자)
        moeim.setAccountNum(generateVirtualAccountNumber());

        // 모임 코드 자동 생성 (6자리 숫자)
        moeim.setMoeimCode(generateMoeimCode());

        // 저장
        return moeimRepository.save(moeim);
    }

    // 가상 계좌 번호 생성 메서드
    private String generateVirtualAccountNumber() {
        Random random = new Random();
        StringBuilder accountNum = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNum.append(random.nextInt(10));
        }
        return accountNum.toString();
    }

    // 모임 코드 생성 메서드 (6자리 숫자)
    private String generateMoeimCode() {
        Random random = new Random();
        StringBuilder moeimCode = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            moeimCode.append(random.nextInt(10));
        }
        return moeimCode.toString();
    }
}
