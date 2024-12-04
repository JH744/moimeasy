package com.kosa.moimeasy.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userid;         // 사용자 ID (응답용)
    private String userName; // 사용자 이름 (요청 및 응답용)
    private String password; // 비밀번호 (요청용, 응답에는 포함하지 않는 것이 보안상 안전)
    private String address;  // 주소 (요청 및 응답용)
    private String email;    // 이메일 (요청 및 응답용)
    private String phone;    // 전화번호 (요청 및 응답용)
    private String nickname; // 닉네임 (요청 및 응답용)
    private Long moeimId;    // 모임 ID (요청 및 응답용)
    private String role;     // 역할 (응답용)
}

