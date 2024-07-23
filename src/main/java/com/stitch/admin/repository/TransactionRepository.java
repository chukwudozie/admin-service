package com.stitch.admin.repository;

import com.stitch.admin.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findAllByDateCreatedBetween(Instant initialDate, Instant finalDate);
    List<Transaction> findTransactionsByDateCreatedWithin(Instant within);
}
