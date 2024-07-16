package com.stitch.admin.repository;

import com.stitch.admin.model.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    PasswordReset findFirstByEmailAddressOrderByDateCreatedDesc(String emailAddress);

    Optional<PasswordReset> findByEmailAddressAndResetCode(String email, String resetCode);
    boolean existsByEmailAddress(String email);
    Optional<PasswordReset> findByEmailAddress(String email);
}
