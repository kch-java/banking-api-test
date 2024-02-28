package ru.astondevs.bankingapitest.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.astondevs.bankingapitest.exception.AccountNotFoundException;
import ru.astondevs.bankingapitest.exception.InsufficientBalanceException;
import ru.astondevs.bankingapitest.exception.InvalidAmountException;
import ru.astondevs.bankingapitest.exception.InvalidNameException;
import ru.astondevs.bankingapitest.exception.InvalidPinException;
import ru.astondevs.bankingapitest.model.Account;
import ru.astondevs.bankingapitest.model.Transaction;
import ru.astondevs.bankingapitest.repository.AccountRepository;
import ru.astondevs.bankingapitest.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void testCreateAccount() {
        // Ожидаемый объект Account
        Account expectedAccount = new Account("Test", "1234");

        // Настраиваем поведение моков
        // Когда save() вызывается для любого объекта Account, возвращаем expectedAccount
        when(accountRepository.save(any(Account.class))).thenReturn(expectedAccount);

        // Вызываем тестируемый метод
        Account actualAccount = accountService.createAccount("Test", "1234");

        // Проверяем, что метод save() репозитория accountRepository вызывается ровно один раз
        // с любым объектом класса Account
        verify(accountRepository, times(1)).save(any(Account.class));

        // Проверяем, что возвращаемый объект Account соответствует ожидаемому
        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void testCreateAccount_EmptyBeneficiaryName() {
        try {
            accountService.createAccount("", "1234");
            fail("Expected an InvalidNameException to be thrown");
        } catch (InvalidNameException e) {
            assertEquals("Beneficiary name must not be empty", e.getMessage());
        }
    }

    @Test
    void testCreateAccount_EmptyPin() {
        try {
            accountService.createAccount("Test", "");
            fail("Expected an InvalidPinException to be thrown");
        } catch (InvalidPinException e) {
            assertEquals("PIN must not be empty", e.getMessage());
        }
    }

    @Test
    void testDeposit() {
        // Создаем объект Account
        Account account = new Account("Test", "1234");

        // Настраиваем поведение моков
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Вызываем тестируемый метод
        accountService.deposit(1L, BigDecimal.valueOf(100));

        // Проверяем, что методы моков вызываются с правильными аргументами
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);

        // Создаем ArgumentCaptor для захвата объекта Transaction
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Проверяем, что транзакция сохраняется в репозитории транзакций и захватываем ее
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        // Получаем захваченную транзакцию
        Transaction savedTransaction = transactionCaptor.getValue();

        // Проверяем, что баланс счета увеличился на правильную сумму
        assertEquals(BigDecimal.valueOf(100), account.getBalance());

        // Проверяем, что создается правильная транзакция
        assertEquals(BigDecimal.valueOf(100), savedTransaction.getAmount());
        assertEquals("deposit", savedTransaction.getType());
        assertEquals(account, savedTransaction.getAccount());
    }

    @Test
    void testDeposit_ZeroAmount() {
        try {
            accountService.deposit(1L, BigDecimal.ZERO);
            fail("Expected an InvalidAmountException to be thrown");
        } catch (InvalidAmountException e) {
            assertEquals("Amount must be greater than zero", e.getMessage());
        }
    }

    @Test
    void testWithdraw_Success() {
        // Создаем объект Account
        Account account = new Account("Test", "1234");
        account.deposit(BigDecimal.valueOf(200));

        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Вызываем тестируемый метод
        accountService.withdraw(1L, "1234", BigDecimal.valueOf(100));

        // Проверяем, что методы мока вызываются с правильными аргументами
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));

        // Создаем ArgumentCaptor для захвата объекта Transaction
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Проверяем, что транзакция сохраняется в репозитории транзакций и захватываем ее
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        // Получаем захваченную транзакцию
        Transaction savedTransaction = transactionCaptor.getValue();

        // Проверяем, что баланс счета уменьшился на правильную сумму
        assertEquals(BigDecimal.valueOf(100), account.getBalance());

        // Проверяем, что создается правильная транзакция
        assertEquals(BigDecimal.valueOf(100), savedTransaction.getAmount());
        assertEquals("withdraw", savedTransaction.getType());
        assertEquals(account, savedTransaction.getAccount());
    }

    @Test
    void testWithdraw_NegativeAmount() {
        try {
            accountService.withdraw(1L, "1234", BigDecimal.valueOf(-100));
            fail("Expected an InvalidAmountException to be thrown");
        } catch (InvalidAmountException e) {
            assertEquals("Amount must be greater than zero", e.getMessage());
        }
    }

    @Test
    void testWithdraw_InsufficientBalance() {
        // Создаем объект Account с начальным балансом 0
        Account account = new Account("Test", "1234");

        // Настраиваем поведение моков
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        try {
            // Пытаемся снять больше денег, чем доступно на счету
            accountService.withdraw(1L, "1234", BigDecimal.valueOf(100));
            fail("Expected an InsufficientBalanceException to be thrown");
        } catch (InsufficientBalanceException e) {
            // Проверяем, что исключение имеет правильное сообщение
            assertEquals("Insufficient balance", e.getMessage());

            // Проверяем, что баланс счета не изменился после попытки снятия
            assertEquals(BigDecimal.ZERO, account.getBalance());
        }
    }

    @Test
    void testWithdraw_InvalidPin() {
        // Создаем объект Account
        Account account = new Account("Test", "1234");
        account.deposit(BigDecimal.valueOf(200));

        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Проверяем, что выбрасывается исключение InvalidPinException при неверном PIN-коде
        assertThrows(InvalidPinException.class, () -> accountService.withdraw(1L, "9999", BigDecimal.valueOf(100)));
    }

    @Test
    void testWithdraw_AccountNotFound() {
        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение AccountNotFoundException при отсутствии счета
        assertThrows(AccountNotFoundException.class, () -> accountService.withdraw(1L, "1234", BigDecimal.valueOf(100)));
    }

    @Test
    void testTransfer() {
        // Создаем два объекта Account
        Account fromAccount = new Account("Test1", "1234");
        fromAccount.deposit(BigDecimal.valueOf(200));
        Account toAccount = new Account("Test2", "5678");

        // Настраиваем поведение моков
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        // Вызываем тестируемый метод
        accountService.transfer(1L, "1234", BigDecimal.valueOf(100), 2L);

        // Проверяем, что методы моков вызываются с правильными аргументами
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));

        // Создаем ArgumentCaptor для захвата объектов Transaction
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Проверяем, что транзакции сохраняются в репозитории транзакций и захватываем их
        verify(transactionRepository, times(2)).save(transactionCaptor.capture());

        // Получаем захваченные транзакции
        List<Transaction> savedTransactions = transactionCaptor.getAllValues();

        // Проверяем, что баланс счета fromAccount уменьшился на правильную сумму
        assertEquals(BigDecimal.valueOf(100), fromAccount.getBalance());

        // Проверяем, что баланс счета toAccount увеличился на правильную сумму
        assertEquals(BigDecimal.valueOf(100), toAccount.getBalance());

        // Проверяем, что создаются правильные транзакции
        for (Transaction savedTransaction : savedTransactions) {
            assertEquals(BigDecimal.valueOf(100), savedTransaction.getAmount());
            assertTrue(savedTransaction.getType().startsWith("transfer"));
            assertTrue(savedTransaction.getAccount().equals(fromAccount) || savedTransaction.getAccount().equals(toAccount));
        }
    }

    @Test
    void testTransfer_NegativeAmount() {
        try {
            // Вызываем тестируемый метод
            accountService.transfer(1L, "1234", BigDecimal.valueOf(-100), 2L);

            // Создаем два объекта Account
            Account fromAccount = new Account("Test1", "1234");
            fromAccount.deposit(BigDecimal.valueOf(200));
            Account toAccount = new Account("Test2", "5678");

            // Настраиваем поведение моков
            when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

            fail("Expected an InvalidAmountException to be thrown");
        } catch (InvalidAmountException e) {
            assertEquals("Amount must be greater than zero", e.getMessage());
        }
    }

    @Test
    void testTransfer_FromAccountNotFound() {
        try {
            // Вызываем тестируемый метод
            accountService.transfer(1L, "1234", BigDecimal.valueOf(100), 2L);

            // Создаем объект Account
            Account toAccount = new Account("Test2", "5678");

            // Настраиваем поведение моков
            when(accountRepository.findById(1L)).thenReturn(Optional.empty());
            when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

            fail("Expected an AccountNotFoundException to be thrown");
        } catch (AccountNotFoundException e) {
            assertEquals("Account with id " + 1L + " not found", e.getMessage());
        }
    }

    @Test
    void testTransfer_ToAccountNotFound() {
        // Создаем объект Account
        Account fromAccount = new Account("Test1", "1234");
        fromAccount.deposit(BigDecimal.valueOf(200));

        // Настраиваем поведение моков
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение AccountNotFoundException при отсутствии счета-получателя
        assertThrows(AccountNotFoundException.class, () -> accountService.transfer(1L, "1234", BigDecimal.valueOf(100), 2L));
    }

    @Test
    void testTransfer_InvalidPin() {
        // Создаем два объекта Account
        Account fromAccount = new Account("Test1", "1234");
        fromAccount.deposit(BigDecimal.valueOf(200));
        Account toAccount = new Account("Test2", "5678");

        // Настраиваем поведение моков
        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        // Проверяем, что выбрасывается исключение InvalidPinException при неверном PIN-коде
        assertThrows(InvalidPinException.class, () -> accountService.transfer(1L, "9999", BigDecimal.valueOf(100), 2L));
    }

    @Test
    void testGetAccount() {
        // Создаем объект Account
        Account account = new Account("Test", "1234");
        account.deposit(BigDecimal.valueOf(200));

        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Вызываем тестируемый метод
        Account result = accountService.getAccount(1L);

        // Проверяем, что метод мока вызывается с правильными аргументами
        verify(accountRepository, times(1)).findById(1L);

        // Проверяем, что возвращается правильный аккаунт
        assertEquals(account, result);
    }

    @Test
    void testGetAccount_AccountNotFound() {
        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение AccountNotFoundException при отсутствии счета
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(1L));
    }

    @Test
    void testGetTransactions() {
        // Создаем объект Account
        Account account = new Account("Test", "1234");
        account.deposit(BigDecimal.valueOf(200));

        // Создаем список транзакций
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction(account, BigDecimal.valueOf(100), "deposit");
        Transaction transaction2 = new Transaction(account, BigDecimal.valueOf(100), "withdraw");
        transactions.add(transaction1);
        transactions.add(transaction2);

        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccount(account)).thenReturn(transactions);

        // Вызываем тестируемый метод
        List<Transaction> result = accountService.getTransactions(1L);

        // Проверяем, что методы мока вызываются с правильными аргументами
        verify(accountRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).findByAccount(account);

        // Проверяем, что возвращается правильный список транзакций
        assertEquals(transactions, result);
    }

    @Test
    void testGetTransactions_AccountNotFound() {
        // Настраиваем поведение мока
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение AccountNotFoundException при отсутствии счета
        assertThrows(AccountNotFoundException.class, () -> accountService.getTransactions(1L));
    }

    @Test
    void testGetAllAccounts() {
        // Создаем список аккаунтов
        List<Account> accounts = new ArrayList<>();
        Account account1 = new Account("Test1", "1234");
        Account account2 = new Account("Test2", "5678");
        accounts.add(account1);
        accounts.add(account2);

        // Настраиваем поведение мока
        when(accountRepository.findAll()).thenReturn(accounts);

        // Вызываем тестируемый метод
        List<Account> result = accountService.getAllAccounts();

        // Проверяем, что метод мока вызывается
        verify(accountRepository, times(1)).findAll();

        // Проверяем, что возвращается правильный список аккаунтов
        assertEquals(accounts, result);
    }

    @Test
    void testGetAllAccountsByBeneficiaryName() {
        // Создаем список аккаунтов для бенефициария "Test1"
        List<Account> accountsTest1 = new ArrayList<>();
        Account account1 = new Account("Test1", "1234");
        Account account2 = new Account("Test1", "5678");
        accountsTest1.add(account1);
        accountsTest1.add(account2);

        // Создаем аккаунт для другого бенефициария
        Account account3 = new Account("Test2", "9012");

        // Настраиваем поведение мока
        when(accountRepository.findByBeneficiaryName("Test1")).thenReturn(accountsTest1);
        when(accountRepository.findByBeneficiaryName("Test2")).thenReturn(Collections.singletonList(account3));

        // Вызываем тестируемый метод для "Test1"
        List<Account> resultTest1 = accountService.getAllAccountsByBeneficiaryName("Test1");

        // Проверяем, что метод мока вызывается
        verify(accountRepository, times(1)).findByBeneficiaryName("Test1");

        // Проверяем, что возвращается правильный список аккаунтов для "Test1"
        assertEquals(accountsTest1, resultTest1);

        // Вызываем тестируемый метод для "Test2"
        List<Account> resultTest2 = accountService.getAllAccountsByBeneficiaryName("Test2");

        // Проверяем, что метод мока вызывается
        verify(accountRepository, times(1)).findByBeneficiaryName("Test2");

        // Проверяем, что возвращается правильный список аккаунтов для "Test2"
        assertEquals(Collections.singletonList(account3), resultTest2);
    }

    @Test
    void testGetAllAccountsByBeneficiaryName_NoAccounts() {
        // Настраиваем поведение мока
        when(accountRepository.findByBeneficiaryName("Test1")).thenReturn(new ArrayList<>());

        // Вызываем тестируемый метод
        List<Account> result = accountService.getAllAccountsByBeneficiaryName("Test1");

        // Проверяем, что метод мока вызывается
        verify(accountRepository, times(1)).findByBeneficiaryName("Test1");

        // Проверяем, что возвращается пустой список
        assertTrue(result.isEmpty());
    }
}
