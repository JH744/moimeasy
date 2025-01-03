package com.kosa.moimeasy.user.dto;

import com.kosa.moimeasy.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String userName;
    private String password;
    private String address;
    private String email;
    private String phone;
    private String nickname;
    private String profileImage;
    private LocalDateTime createAt;
    private Long moeimId;
    private Long roleId; // Role ID 추가
    private String roleName; // Role Name 추가

    private String currentPassword;
    private String confirmPassword;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.password = null; // 비밀번호는 반환하지 않음
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.nickname = user.getNickname();
        this.profileImage=user.getProfileImage();
        this.createAt = user.getCreateAt();
        this.moeimId = user.getMoeimId();
        this.roleId = user.getRole().getRoleId();
        this.roleName = user.getRole().getRoleName();
        //this.roleId = user.getRole() != null ? user.getRole().getRoleName() : null;
    }

//    public UserDTO(Long userId, String nickname, String email) {
//        this.userId = userId;
//        this.nickname = nickname;
//        this.email = email;
//    }
}
