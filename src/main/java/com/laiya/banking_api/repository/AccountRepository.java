package com.laiya.banking_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.laiya.banking_api.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

    public boolean existsByAccountNumber(String accountNumber);
    
    public boolean existsById(Long id);
}
