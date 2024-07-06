package com.stitch.admin.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"password"})
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "first Name is required")
    @Size(min = 3, message = "First Name must be at least 3 characters")
    private String firstName;
    @Size(min = 3, message = "Last Name must be at least 3 characters")
    private String lastName;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;
    @PastOrPresent(message = "Date of birth must be in the past or present")
    private String dateOfBirth;
}
