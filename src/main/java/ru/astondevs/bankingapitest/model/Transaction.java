package ru.astondevs.bankingapitest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

/**
 * Класс Transaction представляет собой модель транзакции в банковском приложении.
 * Он содержит информацию о каждой транзакции, включая счет, тип транзакции, сумму и время проведения транзакции.
 */
@Entity
public class Transaction {

    /**
     * Уникальный идентификатор транзакции.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Счет, с которым связана данная транзакция.
     */
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * Тип транзакции (например, "deposit", "withdraw", "transfer in", "transfer out").
     */
    private String type;

    /**
     * Сумма транзакции.
     */
    private BigDecimal amount;

    /**
     * Время проведения транзакции.
     */
    private String timestamp;

    protected Transaction() {
    }

    public Transaction(Account account, BigDecimal amount, String type) {
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
