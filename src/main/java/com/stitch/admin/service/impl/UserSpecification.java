package com.stitch.admin.service.impl;

import com.stitch.admin.model.entity.Role;
import com.stitch.admin.model.entity.UserEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

    public static Specification<UserEntity> hasRoleName(String roleName) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(roleName) && !"all".equalsIgnoreCase(roleName)) {
                Join<UserEntity, Role> roleJoin = root.join("role");
                return criteriaBuilder.equal(criteriaBuilder.lower(roleJoin.get("name")), roleName.toLowerCase());
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<UserEntity> isEnabled(Boolean enabled) {
        return (root, query, criteriaBuilder) -> {
            if (enabled != null) {
                return criteriaBuilder.equal(root.get("enabled"), enabled);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<UserEntity> hasName(String username) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.hasText(username)) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
            }
            return criteriaBuilder.conjunction();
        };
    }
}
