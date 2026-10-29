package com.laiya.banking_api.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account destinationAccount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    protected Transaction() {
    };

    public Transaction(Account sourceAccount, Account destinationAccount, BigDecimal amount, TransactionType type) {
        setType(type);
        setAmount(amount);
        if (type == TransactionType.DEPOSIT) {
            setSourceAccount(null);
            setDestinationAccount(destinationAccount);
        }
        if (type == TransactionType.WITHDRAWAL) {
            setSourceAccount(sourceAccount);
            setDestinationAccount(null);
        }
        if (type == TransactionType.TRANSFER) {
            setSourceAccount(sourceAccount);
            setDestinationAccount(destinationAccount);
        }
        onCreate();
        setStatus(TransactionStatus.COMPLETED);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
