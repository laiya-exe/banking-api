package com.laiya.banking_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.laiya.banking_api.dto.TransferRequest;
import com.laiya.banking_api.exception.AccountNotFoundException;
import com.laiya.banking_api.model.Account;
import com.laiya.banking_api.repository.AccountRepository;
import com.laiya.banking_api.service.TransactionService;

@SpringBootTest
public class TransferIntegrationTest {
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountRepository accountRepository;

    @Test
    void transactionalTransfert() {
        // Arrange
        Account source = new Account("Awa", "BK-2026-000001");
        source.deposit(BigDecimal.valueOf(1000));
        Account savedAccount = accountRepository.save(source);

        // Act + Assert
        assertThatThrownBy(() -> transactionService
                .transfer(new TransferRequest(savedAccount.getId(), 999L, BigDecimal.valueOf(500))))
                .isInstanceOf(AccountNotFoundException.class);

        // Assert
        Account chargedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();
        assertThat(chargedAccount.getBalance()).isEqualByComparingTo("1000");

    }
}
