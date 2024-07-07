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
@Table(name = "admin_role")
public class Role extends BaseEntity{

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permissions;

    public Role(String name) {
        this.name = name;
    }
}
