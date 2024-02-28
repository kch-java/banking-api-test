package ru.astondevs.bankingapitest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Класс AccountDto представляет собой объект передачи данных для счета.
 * Он содержит информацию о счете, которая может быть передана между слоями приложения.
 */
public class AccountDto {

    @Schema(description = "ID of the account", example = "1")
    private Long id;
    @Schema(description = "Number of the account", example = "1234567890")
    private String accountNumber;
    @Schema(description = "Name of the beneficiary", example = "John Doe")
    private String beneficiaryName;
    @Schema(description = "Balance of the account", example = "1000.00")
    private BigDecimal balance;

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

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
