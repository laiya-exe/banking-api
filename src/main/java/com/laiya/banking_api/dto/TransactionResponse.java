package com.laiya.banking_api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.laiya.banking_api.model.Transaction;
import com.laiya.banking_api.model.TransactionStatus;
import com.laiya.banking_api.model.TransactionType;

public record TransactionResponse(
        Long id,
        TransactionType type,
        BigDecimal amount,
        Long sourceAccountId,
        Long destinationAccountId,
        TransactionStatus status,
        Instant createdAt) {

    public static TransactionResponse from(Transaction transaction) {
        Long sourceId = transaction.getSourceAccount() != null
                ? transaction.getSourceAccount().getId()
                : null;
        Long destinationId = transaction.getDestinationAccount() != null
                ? transaction.getDestinationAccount().getId()
                : null;

        return new TransactionResponse(transaction.getId(), transaction.getType(),
                transaction.getAmount(), sourceId, destinationId, transaction.getStatus(),
                transaction.getCreatedAt());
    }
}
