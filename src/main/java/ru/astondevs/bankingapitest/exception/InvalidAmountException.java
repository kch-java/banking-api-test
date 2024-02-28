package ru.astondevs.bankingapitest.exception;

/**
 * Исключение InvalidAmountException выбрасывается, когда попытка снять средства или выполнить перевод не удается из-за недопустимой суммы операции.
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
