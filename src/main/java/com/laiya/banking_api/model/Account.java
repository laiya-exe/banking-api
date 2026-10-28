package com.laiya.banking_api.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.laiya.banking_api.exception.InsufficientFundsException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity 
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column (nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column (nullable = false, updatable = false)
    private Instant createdAt;

    protected Account(){}
    public Account(String ownerName, String accountNumber){
        setAccountNumber(accountNumber);
        setOwnerName(ownerName);
        onCreate();
    }

    public void setAccountNumber(String number){ this.accountNumber = number;}
    public void setOwnerName(String name){ this.ownerName = name;}

    // public void setBalance(BigDecimal balance){this.balance = balance;}
    public void deposit(BigDecimal amount){ this.balance = this.balance.add(amount);}
    public void withdraw(BigDecimal amount){
        if(this.balance.compareTo(amount) < 0){
            throw new InsufficientFundsException("Pas assez de fonds pour effectuer le retrait.");
        } else {
            this.balance = this.balance.subtract(amount);
        }
    }

    @PrePersist
    protected void onCreate(){ this.createdAt = Instant.now();}

    public Long getId(){return this.id;}
    public String getAccountNumber(){return this.accountNumber;}
    public String getOwnerName(){return this.ownerName;}
    public BigDecimal getBalance(){return this.balance;}
    public Instant getCreatedAt(){return this.createdAt;}

}
