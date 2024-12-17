package com.kosa.moimeasy.settlement.controller;

import com.kosa.moimeasy.settlement.dto.SettlementDTO;
import com.kosa.moimeasy.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    /**
     * 정산 요청 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<SettlementDTO>> getAllRequests(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<SettlementDTO> settlements = settlementService.getAllRequests();
        return ResponseEntity.ok(settlements);
    }

    /**
     * 정산 요청 생성
     */
    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@RequestBody SettlementDTO requestDTO,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        settlementService.createRequest(requestDTO, userId);
        return ResponseEntity.ok("정산 요청이 성공적으로 제출되었습니다.");
    }

    /**
     * 정산 요청 승인
     */
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<?> approveRequest(@PathVariable Long requestId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 인증이 필요합니다.");
        }

        // 관리자인지 검증 (role_name이 "admin"인지 확인)
        if (!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 권한이 필요합니다.");
        }

        settlementService.approveRequest(requestId);
        return ResponseEntity.ok("정산 요청이 승인되었습니다.");
    }

}
