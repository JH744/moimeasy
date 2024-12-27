package com.kosa.moimeasy.moeim.controller;

import com.kosa.moimeasy.moeim.dto.MoeimDTO;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.service.MoeimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;
@Tag(name = "모임페이지", description = "Moeim API")
@RestController
@RequestMapping("/api/v1/moeim")
@CrossOrigin(origins = "http://localhost:3000")
public class MoeimController {

    @Autowired
    private MoeimService moeimService;

    @Operation(summary = "모임 생성", description = "새로운 모임을 생성하는 API")
    @ApiResponse(responseCode = "200", description = "모임 생성 성공")
    @ApiResponse(responseCode = "401", description = "로그인하지 않은 사용자")
    @PostMapping("/create")
    public ResponseEntity<?> createMoeim(@RequestBody MoeimDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        request.setUserId(userId);

        try {
            Moeim createdMoeim = moeimService.createMoeim(request);
            Map<String, Object> response = new HashMap<>();
            response.put("moeimId", createdMoeim.getMoeimId());
            response.put("moeimName", createdMoeim.getMoeimName());
            response.put("moeimCode", createdMoeim.getMoeimCode());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("모임 생성 중 오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "모임 가입", description = "모임에 가입합니다.")
    @ApiResponse(responseCode = "200", description = "모임 가입 성공")
    @PostMapping("/join")
    public ResponseEntity<?> joinMoeim(@RequestBody MoeimDTO request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        request.setUserId(userId);

        try {
            Long moeimId = moeimService.joinMoeim(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "모임 가입이 완료되었습니다.");
            response.put("moeimId", moeimId);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
