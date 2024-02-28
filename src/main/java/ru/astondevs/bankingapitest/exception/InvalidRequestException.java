package ru.astondevs.bankingapitest.exception;

/**
 * Исключение InvalidRequestException выбрасывается, когда попытка выполнить операцию не удается из-за недопустимого запроса.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
