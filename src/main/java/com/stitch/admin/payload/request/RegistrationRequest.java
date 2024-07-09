package com.stitch.admin.payload.request;

import com.stitch.admin.validation.ValidDateFormat;
import com.stitch.admin.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = {"password"})
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Email is required")
    private String emailAddress;
    @NotBlank(message = "Password is required")
    @ValidPassword
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
    @ValidDateFormat
    @PastOrPresent(message = "Date of birth must be in the past or present")
    private LocalDate dateOfBirth;
    @NotBlank(message = "Nationality is required for registration")
    private String nationality;

    private String department;
    private String city;
    private String middleName;
}
