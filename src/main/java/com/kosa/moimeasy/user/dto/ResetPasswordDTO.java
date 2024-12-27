package com.kosa.moimeasy.user.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String name;
    private String phone;
    private String email;
    private String newPassword;
    private String confirmPassword;
}
