package ru.astondevs.bankingapitest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Класс TransactionDto представляет собой объект передачи данных для транзакции.
 * Он содержит информацию о транзакции, которая может быть передана между слоями приложения.
 */
public class TransactionDto {

    @Schema(description = "ID of the transaction", example = "1")
    private Long id;
    @Schema(description = "Number of the account", example = "1234567890")
    private String accountNumber;
    @Schema(description = "Type of the transaction", example = "deposit")
    private String type;
    @Schema(description = "Amount of the transaction", example = "100.00")
    private BigDecimal amount;
    @Schema(description = "Timestamp of the transaction", example = "2022-01-01T00:00:00Z")
    private String timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
