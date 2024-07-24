package com.stitch.admin.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "admin_permissions")
@Audited
public class Permission extends BaseEntity {
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission role = (Permission) o;
        return Objects.nonNull(name) && name.equalsIgnoreCase(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.nonNull(name) ? name.toLowerCase().hashCode() : 0;
    }
}
