package com.kosa.moimeasy.user.controller;


import com.kosa.moimeasy.user.dto.EmailFindRequest;
import com.kosa.moimeasy.user.dto.EmailFindResponse;
import com.kosa.moimeasy.user.exception.UserNotFoundException;
import com.kosa.moimeasy.user.service.FindService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
public class FindController {

    @Autowired
    private FindService findService;

    /**
     * 이메일 찾기 API
     */
    @PostMapping("/find/email")
    public ResponseEntity<EmailFindResponse> findEmail(@Valid @RequestBody EmailFindRequest request) {
        try {
            String email = findService.findEmailByNicknameAndPhone(request.getNickname(), request.getPhone());
            return ResponseEntity.ok(new EmailFindResponse(email));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
