package com.stitch.admin.repository;

import com.stitch.admin.model.entity.BodyMeasurement;
import com.stitch.admin.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {
    Optional<BodyMeasurement> findByCustomer(Customer customer);

//    @Query("SELECT bm FROM BodyMeasurement bm WHERE bm.customer = :customer")
//    Optional<BodyMeasurement> findByCustomer(@Param("customer") Customer customer);
}
