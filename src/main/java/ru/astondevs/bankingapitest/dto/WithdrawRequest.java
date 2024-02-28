package ru.astondevs.bankingapitest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * Класс WithdrawRequest представляет собой объект передачи данных для операции снятия.
 * Он содержит информацию, необходимую для выполнения операции снятия.
 */
public class WithdrawRequest {

    @Schema(description = "PIN of the account", example = "1234")
    @NotBlank(message = "PIN must not be empty")
    private String pin;

    @Schema(description = "Amount to withdraw", example = "100.00")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

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
}
