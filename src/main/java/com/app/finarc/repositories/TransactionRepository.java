package com.app.finarc.repositories;


import com.app.finarc.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    Page<Transaction> findByUserId(String userId, Pageable pageable);
}
