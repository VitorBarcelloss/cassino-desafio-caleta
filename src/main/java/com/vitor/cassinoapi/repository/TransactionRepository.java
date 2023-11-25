package com.vitor.cassinoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vitor.cassinoapi.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    
}
