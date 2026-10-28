package com.laiya.banking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.laiya.banking_api.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

}
