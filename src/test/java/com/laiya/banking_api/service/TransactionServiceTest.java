package com.laiya.banking_api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.laiya.banking_api.dto.TransferRequest;
import com.laiya.banking_api.exception.AccountNotFoundException;
import com.laiya.banking_api.exception.InsufficientFundsException;
import com.laiya.banking_api.exception.InvalidTransferException;
import com.laiya.banking_api.model.Account;
import com.laiya.banking_api.model.Transaction;
import com.laiya.banking_api.repository.AccountRepository;
import com.laiya.banking_api.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    TransactionRepository transactionRepository;
    @InjectMocks
    TransactionService transactionService;

    @Test
    void transfer_shouldMoveMoney_whenFundsAreSufficient() {
        // Arrange
        Account source = new Account("Awa", "BK-2026-000001");
        source.deposit(BigDecimal.valueOf(10000));
        Account destination = new Account("Moussa", "BK-2026-000002");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destination));

        // Act
        transactionService.transfer(new TransferRequest(1L, 2L, new BigDecimal("3000")));

        // Assert
        assertThat(source.getBalance()).isEqualByComparingTo("7000");
        assertThat(destination.getBalance()).isEqualByComparingTo("3000");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldFail_whenInsufficientFunds() {
        // Arrange : source avec 1000, destination vide
        Account source = new Account("Awa", "BK-2026-000001");
        source.deposit(BigDecimal.valueOf(1000));
        Account destination = new Account("Moussa", "BK-2026-000002");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destination));

        // Act + Assert : un virement de 5 000 doit échouer
        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transfer(new TransferRequest(1L, 2L, BigDecimal.valueOf(5000))));

        // Assert : rien n'a bougé
        assertThat(source.getBalance()).isEqualByComparingTo("1000");
        assertThat(destination.getBalance()).isEqualByComparingTo("0");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transfer_shouldFail_whenSourceIsDestination() {
        // // Arrange. (Pas besoin d'arrange ici)
        // Account source = new Account("Awa", "BK-2026-000001");

        // Act + Assert
        assertThrows(InvalidTransferException.class,
                () -> transactionService.transfer(new TransferRequest(1L, 1L, BigDecimal.valueOf(1000))));
        verifyNoInteractions(accountRepository);
    }

    @Test
    void transfer_shouldFail_whenAccountNotFound() {
        // Arrange
        Account source = new Account("Awa", "BK-2026-000001");
        source.deposit(BigDecimal.valueOf(1000));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // Act + Assert : on attend AccountNotFOundException
        assertThrows(AccountNotFoundException.class,
                () -> transactionService.transfer(new TransferRequest(1L, 2L, BigDecimal.valueOf(500))));

        // Assert : rien n'a bougé
        assertThat(source.getBalance()).isEqualByComparingTo("1000");
        verify(transactionRepository, never()).save(any());
    }
}