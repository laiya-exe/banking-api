package com.laiya.banking_api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.laiya.banking_api.model.Account;

public record AccountResponse(
        Long id,
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        Instant createdAt) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(),
                account.getOwnerName(), account.getBalance(), account.getCreatedAt());
    }
}
