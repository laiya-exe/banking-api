package com.laiya.banking_api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
    @NotNull(message = "Montant requis.")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0.01.")
    BigDecimal amount
) {}
