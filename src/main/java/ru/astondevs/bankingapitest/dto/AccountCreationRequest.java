package ru.astondevs.bankingapitest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Класс AccountCreationRequest представляет собой объект передачи данных для создания счета.
 * Он содержит информацию, необходимую для создания нового счета.
 */
public class AccountCreationRequest {

    @Schema(description = "Name of the beneficiary", example = "John Doe")
    @NotBlank
    private String beneficiaryName;
    @Schema(description = "PIN of the account", example = "1234")
    @NotBlank
    private String pin;

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
