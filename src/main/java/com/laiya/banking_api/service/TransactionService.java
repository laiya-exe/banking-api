package com.laiya.banking_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laiya.banking_api.dto.TransactionResponse;
import com.laiya.banking_api.dto.TransfertRequest;
import com.laiya.banking_api.exception.AccountNotFoundException;
import com.laiya.banking_api.exception.InvalidTransferException;
import com.laiya.banking_api.model.Account;
import com.laiya.banking_api.model.Transaction;
import com.laiya.banking_api.model.TransactionType;
import com.laiya.banking_api.repository.AccountRepository;
import com.laiya.banking_api.repository.TransactionRepository;

@Service
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse transfer(TransfertRequest request) {
        if (request.sourceAccountId().compareTo(request.destinationAccountId()) == 0)
            throw new InvalidTransferException("Virement vers le même compte refusé.");

        Account sourceAccount = getById(request.sourceAccountId());
        Account destinationAccount = getById(request.destinationAccountId());

        sourceAccount.withdraw(request.amount());
        destinationAccount.deposit(request.amount());

        Transaction transaction = new Transaction(sourceAccount, destinationAccount, request.amount(),
                TransactionType.TRANSFER);
        transactionRepository.save(transaction);

        return TransactionResponse.from(transaction);
    }

    // Fonctions utilitaires
    public Account getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Compte introuvable."));
        return account;
    }
}
