package com.stitch.admin.repository;

import com.stitch.admin.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    long countUserEntitiesByEnabledTrue();

    long countUserEntitiesByRole_NameIgnoreCase(String roleName);
    long countUserEntitiesByEnabledTrueAndRole_NameIgnoreCase(String roleName);

}
