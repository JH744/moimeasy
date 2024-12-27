package com.kosa.moimeasy.settlement.service;

import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.settlement.dto.SettlementDTO;
import com.kosa.moimeasy.settlement.entity.Settlement;
import com.kosa.moimeasy.settlement.entity.Settlement.SettlementStatus;
import com.kosa.moimeasy.settlement.repository.SettlementRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final MoeimRepository moeimRepository;

    /**
     * 모든 정산 요청 조회
     */
    @Transactional
    public List<SettlementDTO> getAllRequests() {
        return settlementRepository.findAll()
                .stream()
                .map(settlement -> {
                    SettlementDTO dto = new SettlementDTO();
                    dto.setId(settlement.getId()); // ID 추가
                    dto.setTitle(settlement.getTitle());
                    dto.setUserName(settlement.getUser() != null ? settlement.getUser().getUserName() : "알 수 없는 사용자");
                    dto.setImageUrl(settlement.getImageUrl());
                    dto.setAmount(settlement.getAmount());
                    dto.setCreatedAt(settlement.getCreatedAt() != null ? settlement.getCreatedAt().toString() : "N/A");
                    dto.setStatus(settlement.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }




    /**
     * 정산 요청 생성
     */
    @Transactional
    public void createRequest(SettlementDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Settlement settlement = new Settlement(
                requestDTO.getTitle(),
                requestDTO.getImageUrl(),
                user,
                requestDTO.getAmount()
        );

        settlementRepository.save(settlement);
    }

    /**
     * 정산 요청 승인
     */
    @Transactional
    public void approveRequest(Long requestId) {
        Settlement settlement = settlementRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("정산 요청을 찾을 수 없습니다."));

        User user = settlement.getUser();
        Moeim moeim = moeimRepository.findById(user.getMoeimId())
                .orElseThrow(() -> new IllegalArgumentException("모임 정보를 찾을 수 없습니다."));

        double requestedAmount = settlement.getAmount();

        // 모임 계좌 잔액 확인
        if (moeim.getAmount() < requestedAmount) {
            throw new IllegalArgumentException("모임 계좌에 잔액이 부족합니다.");
        }

        // 정산 처리: 모임 계좌 → 사용자 계좌
        moeim.setAmount(moeim.getAmount() - requestedAmount);
        user.setAmount(user.getAmount() + requestedAmount);

        settlement.setStatus(SettlementStatus.ACCEPTED);

        // 변경사항 저장
        settlementRepository.save(settlement);
        moeimRepository.save(moeim);
        userRepository.save(user);
    }

    /**
     * 정산 요청 거부
     */
    @Transactional
    public void rejectRequest(Long requestId) {
        Settlement settlement = settlementRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("정산 요청을 찾을 수 없습니다."));

        settlement.setStatus(SettlementStatus.REJECTED);
        settlementRepository.save(settlement);
    }
}
