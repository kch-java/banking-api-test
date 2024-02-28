package ru.astondevs.bankingapitest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.bankingapitest.exception.AccountNotFoundException;
import ru.astondevs.bankingapitest.exception.InvalidAmountException;
import ru.astondevs.bankingapitest.exception.InvalidNameException;
import ru.astondevs.bankingapitest.exception.InvalidPinException;
import ru.astondevs.bankingapitest.model.Account;
import ru.astondevs.bankingapitest.model.Transaction;
import ru.astondevs.bankingapitest.repository.AccountRepository;
import ru.astondevs.bankingapitest.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Класс AccountService предоставляет сервисы для работы со счетами.
 * Он содержит методы для создания счетов, снятия и перевода средств, а также получения информации о счетах и транзакциях.
 */
@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Создает новый счет с указанным именем владельца и PIN-кодом.
     *
     * @param beneficiaryName имя владельца счета
     * @param pin             PIN-код счета
     * @return Созданный объект счета
     */
    public Account createAccount(String beneficiaryName, String pin) {
        if (beneficiaryName == null || beneficiaryName.trim().isEmpty()) {
            throw new InvalidNameException("Beneficiary name must not be empty");
        }
        if (beneficiaryName.length() > 50) {
            throw new InvalidNameException("Beneficiary name must not be longer than 50 characters");
        }
        if (!beneficiaryName.matches("[a-zA-Z0-9 ]*")) {
            throw new InvalidNameException("Beneficiary name contains invalid characters");
        }
        validatePin(pin);
        Account account = new Account(beneficiaryName, pin);
        return accountRepository.save(account);
    }

    /**
     * Вносит указанную сумму на счет.
     *
     * @param accountId идентификатор счета, на который будут внесены средства
     * @param amount    сумма, которую нужно внести
     * @return Обновленный объект счета после внесения средств
     */
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        validateAmount(amount);

        Account account = getAccount(accountId);
        account.deposit(amount);
        Transaction transaction = new Transaction(account, amount, "deposit");
        transactionRepository.save(transaction);
        account = accountRepository.save(account);
        logger.info("Depositing {} to account {}", amount, accountId);
        return account;
    }

    /**
     * Снимает указанную сумму со счета.
     *
     * @param accountId идентификатор счета, с которого будут сняты средства
     * @param pin       PIN-код для проверки
     * @param amount    сумма, которую нужно снять
     * @return Обновленный объект счета после снятия средств
     */
    @Transactional
    public Account withdraw(Long accountId, String pin, BigDecimal amount) {
        validatePin(pin);
        validateAmount(amount);

        Account account = getAccount(accountId);
        if (!account.getPin().equals(pin)) {
            throw new InvalidPinException("Invalid PIN");
        }
        account.withdraw(pin, amount);
        Transaction transaction = new Transaction(account, amount, "withdraw");
        transactionRepository.save(transaction);
        account = accountRepository.save(account);
        logger.info("Withdrawing {} from account {}", amount, accountId);
        return account;
    }

    /**
     * Переводит указанную сумму с одного счета на другой.
     *
     * @param fromAccountId идентификатор счета, с которого будут переведены средства
     * @param pin           PIN-код для проверки
     * @param amount        сумма, которую нужно перевести
     * @param toAccountId   идентификатор счета, на который будут переведены средства
     * @return Обновленный объект счета после перевода средств
     */
    @Transactional
    public Account transfer(Long fromAccountId, String pin, BigDecimal amount, Long toAccountId) {
        validatePin(pin);
        validateAmount(amount);

        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);
        if (!fromAccount.getPin().equals(pin)) {
            throw new InvalidPinException("Invalid PIN");
        }
        fromAccount.transfer(pin, amount, toAccount);
        Transaction transactionFrom = new Transaction(fromAccount, amount, "transfer out");
        Transaction transactionTo = new Transaction(toAccount, amount, "transfer in");
        transactionRepository.save(transactionFrom);
        transactionRepository.save(transactionTo);
        accountRepository.save(toAccount);
        fromAccount = accountRepository.save(fromAccount);
        logger.info("Transferring {} from account {} to account {}", amount, fromAccountId, toAccountId);
        return fromAccount;
    }

    /**
     * Метод для получения информации о счете по его идентификатору.
     *
     * @param accountId идентификатор счета
     * @return Объект счета, соответствующий указанному идентификатору
     * @throws AccountNotFoundException если счет с указанным идентификатором не найден
     */
    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + accountId + " not found"));
    }

    /**
     * Метод для получения списка всех транзакций для указанного счета.
     *
     * @param accountId идентификатор счета
     * @return Список всех транзакций для указанного счета
     * @throws AccountNotFoundException если счет с указанным идентификатором не найден
     */
    public List<Transaction> getTransactions(Long accountId) {
        Account account = getAccount(accountId);
        return transactionRepository.findByAccount(account);
    }

    /**
     * Метод для получения списка всех счетов.
     *
     * @return Список всех счетов
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Метод для получения списка всех счетов, принадлежащих указанному владельцу.
     *
     * @param beneficiaryName имя владельца счета
     * @return Список всех счетов, принадлежащих указанному владельцу
     */
    public List<Account> getAllAccountsByBeneficiaryName(String beneficiaryName) {
        return accountRepository.findByBeneficiaryName(beneficiaryName);
    }

    private void validatePin(String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            throw new InvalidPinException("PIN must not be empty");
        }
        if (!pin.matches("\\d{4}")) {
            throw new InvalidPinException("PIN must be a 4-digit number");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }
}
