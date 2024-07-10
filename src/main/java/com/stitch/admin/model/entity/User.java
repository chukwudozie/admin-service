package com.stitch.admin.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"password"})
@MappedSuperclass
public abstract class User extends BaseEntity {


    @Column(name = "first_name", nullable = false)
    protected String firstName;

    @Column(name = "last_name", nullable = false)
    protected String lastName;

    @Column(name = "middle_name")
    protected String middleName;

    @Column(name = "email_address", unique = true)
    protected String emailAddress;

    @Column(name = "phone_number", unique = true, nullable = false)
    protected String phoneNumber;

    @Column(name = "password")
    @JsonIgnore
    protected String password;

    @Column(name = "last_login")
    protected Instant lastLogin;

    @Column(name = "expired_password")
    protected boolean expiredPassword;

    @Column(name = "last_password_change")
    protected Instant lastPasswordChange;

    @Column(name = "is_enabled")
    protected boolean enabled = true;

    @Column(name = "login_attempts")
    protected int loginAttempts;

    @Column(name = "account_locked")
    protected boolean accountLocked;

    @Column(name = "nationality")
    protected String nationality;

}
