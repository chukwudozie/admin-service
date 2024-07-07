package com.stitch.admin.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_user")
public class AdminUser extends User{
    private boolean activated;
    private String department;
    private String grade;
    private double salary;
    private String image;
    private String dateOfBirth;
    private int age;
    private String city;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
