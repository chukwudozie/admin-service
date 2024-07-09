package com.stitch.admin.repository;

import com.stitch.admin.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

    Optional<Permission> findByNameIgnoreCase(String permissionName);

    boolean existsByNameIgnoreCase(String name);
}
