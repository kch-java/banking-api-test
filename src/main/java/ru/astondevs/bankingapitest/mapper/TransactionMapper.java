package ru.astondevs.bankingapitest.mapper;

import org.springframework.stereotype.Component;
import ru.astondevs.bankingapitest.dto.TransactionDto;
import ru.astondevs.bankingapitest.model.Transaction;

/**
 * Класс TransactionMapper предназначен для преобразования объектов типа Transaction в объекты типа TransactionDto.
 * Это позволяет изолировать внутреннее представление данных от представления, используемого для взаимодействия с внешним миром.
 */
@Component
public class TransactionMapper {

    /**
     * Преобразует объект типа Transaction в объект типа TransactionDto.
     *
     * @param transaction объект типа Transaction для преобразования.
     * @return объект типа TransactionDto, соответствующий входному объекту типа Transaction.
     */
    public TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setTimestamp(transaction.getTimestamp());
        return dto;
    }
}
