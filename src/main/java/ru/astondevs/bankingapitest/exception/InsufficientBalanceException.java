package ru.astondevs.bankingapitest.exception;

/**
 * Исключение InsufficientBalanceException выбрасывается, когда попытка снять средства или выполнить перевод не удается из-за недостаточного баланса на счете.
 */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
