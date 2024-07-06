package com.stitch.admin.repository;

import com.stitch.admin.model.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByPhoneNumber(String phoneNumber);

    Optional<Vendor> findByEmailAddress(String emailAddress);

    Optional<Vendor> findByBusinessName(String businessName);

    Optional<Vendor> findByVendorId(String vendorId);
}
