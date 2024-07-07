package com.stitch.admin.repository;

import com.stitch.admin.model.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository  extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findAdminUserByEmailAddress(String email);
    Optional<AdminUser> findAdminUserByPhoneNumber(String phoneNumber);
}
