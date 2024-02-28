package ru.astondevs.bankingapitest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.astondevs.bankingapitest.exception.AccountNotFoundException;
import ru.astondevs.bankingapitest.exception.InsufficientBalanceException;
import ru.astondevs.bankingapitest.exception.InvalidAmountException;
import ru.astondevs.bankingapitest.exception.InvalidNameException;
import ru.astondevs.bankingapitest.exception.InvalidPinException;
import ru.astondevs.bankingapitest.exception.InvalidRequestException;

/**
 * Класс GlobalExceptionHandler обрабатывает исключения на глобальном уровне в приложении.
 * Он перехватывает различные типы исключений и возвращает соответствующие сообщения об ошибках и статусы HTTP.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(e.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(InvalidAmountException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidNameException.class)
    public ResponseEntity<String> handleInvalidNameException(InvalidNameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPinException(InvalidPinException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = "Неверный формат JSON в запросе";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
