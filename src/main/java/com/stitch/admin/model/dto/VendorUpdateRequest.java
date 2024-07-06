package com.stitch.admin.model.dto;


import lombok.Data;

@Data
public class VendorUpdateRequest {

    private String firstName;

    private String lastName;

    private String middleName;

    private String emailAddress;

    private String phoneNumber;

    private String password;
    private String businessName;
    private String nationality;

    private String profileImage;

    private String vendorId;

    private String pin;

    private String country;

}
