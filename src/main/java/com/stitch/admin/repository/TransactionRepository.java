package com.stitch.admin.repository;

import com.stitch.admin.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findAllByDateCreatedBetween(Instant initialDate, Instant finalDate);

//    long countTransaction
}
