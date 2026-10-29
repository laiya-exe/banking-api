package com.laiya.banking_api.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laiya.banking_api.dto.AccountResponse;
import com.laiya.banking_api.dto.CreateAccountRequest;
import com.laiya.banking_api.dto.TransactionResponse;
import com.laiya.banking_api.exception.AccountNotFoundException;
import com.laiya.banking_api.model.Account;
import com.laiya.banking_api.model.Transaction;
import com.laiya.banking_api.model.TransactionType;
import com.laiya.banking_api.repository.AccountRepository;
import com.laiya.banking_api.repository.TransactionRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        String generatedAccountNumber;
        boolean exits;

        // Generer un numero de compte unique
        do {
            generatedAccountNumber = accountNumberGenerator();
            exits = accountRepository.existsByAccountNumber(generatedAccountNumber);
        } while (exits);

        // Creer le compte
        Account account = new Account(request.ownerName(), generatedAccountNumber);
        Account savedAccount = accountRepository.save(account);
        return AccountResponse.from(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        return AccountResponse.from(getById(id));
    }

    @Transactional
    public AccountResponse deposit(Long id, BigDecimal amount) {
        Account destinationAccount = getById(id);
        destinationAccount.deposit(amount);

        // Creer la transaction et eneregistrer le tout
        Transaction transaction = new Transaction(null, destinationAccount, amount, TransactionType.DEPOSIT);
        transactionRepository.save(transaction);

        return AccountResponse.from(destinationAccount);
    }

    public AccountResponse withdraw(Long id, BigDecimal amount) {
        Account sourceAccount = getById(id);
        sourceAccount.withdraw(amount);

        // Creer la transaction et enregistrer le tout
        Transaction transaction = new Transaction(sourceAccount, null, amount, TransactionType.WITHDRAWAL);
        transactionRepository.save(transaction);

        return AccountResponse.from(sourceAccount);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionHistory(Long id) {
        List<Transaction> history = transactionRepository
                .findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(id, id);

        return history.stream().map(TransactionResponse::from).toList();
    }

    // Fonctions utilitaires
    public Account getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Compte introuvable."));
        return account;
    }

    private String accountNumberGenerator() {
        // Format : BK-2026-849201
        final SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000);
        return "BK-2026-" + number;
    }
}