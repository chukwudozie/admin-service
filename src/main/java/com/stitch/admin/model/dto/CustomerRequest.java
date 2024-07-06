package com.stitch.admin.model.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String password;
    private String country;
    private DeviceDto device;
    private String profileImage;
    private String currency;
    @NonNull
    private String username;
}
