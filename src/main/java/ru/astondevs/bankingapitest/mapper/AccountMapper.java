package ru.astondevs.bankingapitest.mapper;

import org.springframework.stereotype.Component;
import ru.astondevs.bankingapitest.dto.AccountDto;
import ru.astondevs.bankingapitest.model.Account;

/**
 * Класс AccountMapper предназначен для преобразования объектов типа Account в объекты типа AccountDto.
 * Это позволяет изолировать внутреннее представление данных от представления, используемого для взаимодействия с внешним миром.
 */
@Component
public class AccountMapper {

    /**
     * Преобразует объект типа Account в объект типа AccountDto.
     *
     * @param account объект типа Account для преобразования.
     * @return объект типа AccountDto, соответствующий входному объекту типа Account.
     */
    public AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBeneficiaryName(account.getBeneficiaryName());
        dto.setBalance(account.getBalance());
        return dto;
    }
}
