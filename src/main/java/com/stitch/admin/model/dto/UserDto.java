package com.stitch.admin.model.dto;


import com.stitch.admin.model.entity.*;
import com.stitch.admin.model.enums.Tier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String emailAddress;
    private String phoneNumber;
    private String lastLogin;
    private boolean expiredPassword;
    private String lastPasswordChange;
    private boolean enabled;
    private Integer loginAttempts;
    private String nationality;
    private String username;
    private String profileImage;
    private String userId;
    private String pin;
    private Integer pinAttempts = 0;
    private Tier tier = Tier.ONE;
    private String country;
    private String referredBy;
    private boolean saveCard;
    private boolean enablePush;
    private Role role;
    private Device device;
    private Address address;
    private IdentityDocument identityDocument;
    private BodyMeasurement bodyMeasurement;
    private boolean accountLocked;
    private Instant dateCreated;
    private Instant lastUpdated;


}
