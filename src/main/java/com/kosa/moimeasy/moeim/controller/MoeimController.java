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

@RestController
@RequestMapping("/api/v1/moeim")
@CrossOrigin(origins = "http://localhost:3000")
public class MoeimController {

    @Autowired
    private MoeimService moeimService;

    @PostMapping("/create")
    public ResponseEntity<Moeim> createMoeim(@RequestBody MoeimDTO request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername()); // UserDetails에서 userId 가져오기
        request.setUserId(userId);

        Moeim createdMoeim = moeimService.createMoeim(request);
        return ResponseEntity.ok(createdMoeim);
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinMoeim(@RequestBody MoeimDTO request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        request.setUserId(userId);

        try {
            moeimService.joinMoeim(request);
            return ResponseEntity.ok("모임 가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
