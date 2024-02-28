package ru.astondevs.bankingapitest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Класс TransferRequest представляет собой объект передачи данных для операции перевода.
 * Он содержит информацию, необходимую для выполнения операции перевода.
 */
public class TransferRequest {

    @Schema(description = "PIN of the account", example = "1234")
    @NotBlank(message = "PIN must not be empty")
    private String pin;

    @Schema(description = "Amount to transfer", example = "100.00")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Schema(description = "ID of the account to transfer to", example = "2")
    @NotNull(message = "To Account ID must not be null")
    private Long toAccountId;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }
}
