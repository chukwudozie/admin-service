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
public class PasswordUpdateRequest {
    @NotNull(message = "Email is required")
    @ValidEmail
    private String emailAddress;

    @NotNull(message = "old password  is required")
    private String oldPassword;

    @NotNull(message = "New password is required")
    @ValidPassword
    private String newPassword;

    @ValidPassword
    @NotNull(message = "newPassword is required")
    private String confirmPassword;
}
