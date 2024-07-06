package com.stitch.admin.repository;

import com.stitch.admin.model.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    PasswordReset findFirstByEmailAddressOrderByDateCreatedDesc(String emailAddress);
}
