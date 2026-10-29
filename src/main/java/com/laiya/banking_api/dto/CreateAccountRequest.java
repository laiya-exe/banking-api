package com.laiya.banking_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
        @NotBlank(message = "Un nom de compte est requis.") String ownerName) {
}
