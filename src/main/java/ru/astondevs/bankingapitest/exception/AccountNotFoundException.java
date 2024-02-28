package ru.astondevs.bankingapitest.exception;

/**
 * Исключение AccountNotFoundException выбрасывается, когда попытка доступа к счету не удается из-за отсутствия счета.
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
