package com.laiya.banking_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laiya.banking_api.dto.TransactionResponse;
import com.laiya.banking_api.dto.TransfertRequest;
import com.laiya.banking_api.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transfers")
public class TransfertController {

    private final TransactionService transactionService;

    public TransfertController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransfert(@Valid @RequestBody TransfertRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
