package com.laiya.banking_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laiya.banking_api.dto.AccountResponse;
import com.laiya.banking_api.dto.CreateAccountRequest;
import com.laiya.banking_api.dto.TransactionRequest;
import com.laiya.banking_api.dto.TransactionResponse;
import com.laiya.banking_api.service.AccountService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        AccountResponse response = accountService.getAccount(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        AccountResponse response = accountService.deposit(id, request.amount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        AccountResponse response = accountService.withdraw(id, request.amount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> transactionsHistory(@PathVariable Long id) {
        List<TransactionResponse> response = accountService.getTransactionHistory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
