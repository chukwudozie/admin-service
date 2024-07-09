package com.stitch.admin.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.nonNull(name) && name.equalsIgnoreCase(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.nonNull(name) ? name.toLowerCase().hashCode() : 0;
    }
}
