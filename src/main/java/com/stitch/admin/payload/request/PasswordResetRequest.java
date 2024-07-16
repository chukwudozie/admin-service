package com.stitch.admin.payload.request;

import com.stitch.admin.validation.PasswordMatches;
import com.stitch.admin.validation.ValidEmail;
import com.stitch.admin.validation.ValidPassword;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@PasswordMatches
public class PasswordResetRequest {

    @NotNull(message = "Email is required")
    @ValidEmail
    private String emailAddress;
    @NotNull(message = "Password Reset OTP is required")
    private String resetCode;
    @NotNull(message = "newPassword is required")
    @ValidPassword
    private String newPassword;
    @NotNull(message = "confirmPassword is required")
    @ValidPassword
    private String confirmPassword;
}
