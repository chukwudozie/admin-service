package com.stitch.admin.repository;

import com.stitch.admin.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailAddress(String emailAddress);

    Optional<Customer> findByEmailAddress(String emailAddress);

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByCustomerId(String customerId);
}
