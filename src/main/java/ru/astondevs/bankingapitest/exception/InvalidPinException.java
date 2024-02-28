package ru.astondevs.bankingapitest.exception;

/**
 * Исключение InvalidPinException выбрасывается, когда попытка снять средства или выполнить перевод не удается из-за недопустимого PIN-кода.
 */
public class InvalidPinException extends RuntimeException {
    public InvalidPinException(String message) {
        super(message);
    }
}
