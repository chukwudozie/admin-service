package com.stitch.admin.payload.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String country;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationality;
    private String department;
    private String city;
    private String middleName;
    private String image;

}
