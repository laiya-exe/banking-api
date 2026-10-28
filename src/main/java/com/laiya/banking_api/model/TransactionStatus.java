package com.laiya.banking_api.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public enum TransactionStatus {
    @Enumerated(EnumType.STRING)
    COMPLETED,
    @Enumerated(EnumType.STRING)
    PENDING,
    @Enumerated(EnumType.STRING)
    FAILED
}
