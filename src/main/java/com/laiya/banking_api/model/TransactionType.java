package com.laiya.banking_api.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum TransactionType {
    @Enumerated(EnumType.STRING)
    DEPOSIT,
    @Enumerated(EnumType.STRING)
    WITHDRAWAL,
    @Enumerated(EnumType.STRING)
    TRANSFER
}
