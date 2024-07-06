package com.stitch.admin.model.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {

    private String emailAddress;
    private String resetCode;
    private String newPassword;
    private String confirmPassword;
}
