package ru.astondevs.bankingapitest.exception;

/**
 * Исключение InvalidNameException выбрасывается, когда попытка создать счет не удается из-за недопустимого имени владельца.
 */
public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String message) {
        super(message);
    }
}
