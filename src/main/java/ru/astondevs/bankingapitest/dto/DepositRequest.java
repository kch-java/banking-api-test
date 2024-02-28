package ru.astondevs.bankingapitest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * Класс DepositRequest представляет собой объект передачи данных для операции депозита.
 * Он содержит информацию, необходимую для выполнения операции депозита.
 */
public class DepositRequest {

    @Schema(description = "Amount to deposit", example = "100.00")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
