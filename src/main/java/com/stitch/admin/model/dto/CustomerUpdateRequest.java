package com.stitch.admin.model.dto;

import lombok.Data;

@Data
public class CustomerUpdateRequest {

    private String firstName;
    private String lastName;
    private String country;
    private String profileImage;
}
