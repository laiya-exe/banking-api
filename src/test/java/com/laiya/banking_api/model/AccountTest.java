package com.laiya.banking_api.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.laiya.banking_api.exception.InsufficientFundsException;

public class AccountTest {

    @Test
    void deposit_shouldIncreaseBalance() {
        // Arrange - Preparation : compte
        Account account = new Account("Awa", "BK-2026-000001");

        // Act - Appel
        account.deposit(BigDecimal.valueOf(1000));

        // Assert - Verification
        assertThat(account.getBalance()).isEqualByComparingTo("1000");
    }

    @Test
    void withdraw_souldThrow_whenBalanceTooLow() {
        // Preparation : compte avec 1000
        Account account = new Account("Awa", "BK-2026-000002");
        account.deposit(BigDecimal.valueOf(1000));

        // Appel et Vérification : un retrait de 5000 doit lever l'exeption
        assertThrows(InsufficientFundsException.class,
                () -> account.withdraw(BigDecimal.valueOf(5000)));

        // Verifier que le solde n'a pas bouge
        assertThat(account.getBalance()).isEqualByComparingTo("1000");
    }
}
