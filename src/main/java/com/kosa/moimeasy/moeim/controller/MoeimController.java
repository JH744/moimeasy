package com.kosa.moimeasy.moeim.controller;

import com.kosa.moimeasy.moeim.dto.MoeimDTO;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.service.MoeimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/moeim")
@CrossOrigin(origins = "http://localhost:3000")
public class MoeimController {

    @Autowired
    private MoeimService moeimService;

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
