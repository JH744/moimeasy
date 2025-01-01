package com.kosa.moimeasy.settlement.controller;

import com.kosa.moimeasy.settlement.dto.SettlementDTO;
import com.kosa.moimeasy.settlement.service.SettlementService;
import com.kosa.moimeasy.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.kosa.moimeasy.user.repository.UserRepository;
import java.util.List;


@Tag(name = "정산페이지", description = "Settle API")
@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    /**
     * 정산 요청 조회
     */
    @Operation(summary = "정산 요청조회", description = "회원들의 정산 요청을 보여주는 API")
    @ApiResponse(responseCode = "200", description = "정산 요청 조회 성공")
    @ApiResponse(responseCode = "401", description = "로그인하지 않은 사용자")
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
    @Operation(summary = "정산 요청", description = "정산을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "정산 요청 성공")
    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@RequestBody SettlementDTO requestDTO,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
        }

        if (requestDTO.getCategoryName() == null || requestDTO.getCategoryName().isEmpty()) {
            return ResponseEntity.badRequest().body("제목을 입력해 주세요.");
        }
        if (requestDTO.getImageUrl() == null || requestDTO.getImageUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("영수증 이미지를 첨부해 주세요.");
        }
        if (requestDTO.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("요청 금액은 0보다 커야 합니다.");
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        settlementService.createRequest(requestDTO, userId);

        return ResponseEntity.ok("정산 요청이 성공적으로 제출되었습니다.");
    }



    private final UserRepository userRepository;

    /**
     * 정산 요청 승인
     */
    @Operation(summary = "정산 요청 승인", description = "요청된 정산을 승인합니다.")
    @ApiResponse(responseCode = "200", description = "정산 승인 성공")
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 인증이 필요합니다.");
        }

        // 현재 로그인된 사용자 ID 가져오기
        Long userId = Long.valueOf(userDetails.getUsername());

        // 데이터베이스에서 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // roleId 확인 (Role 객체를 통해 접근)
        if (!"admin".equalsIgnoreCase(user.getRole().getRoleName())) { // 관리자인지 확인
            // return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 권한이 필요합니다.");
        }

        // 정산 승인 처리
        settlementService.approveRequest(requestId);
        return ResponseEntity.ok("정산 요청이 승인되었습니다.");
    }





}
