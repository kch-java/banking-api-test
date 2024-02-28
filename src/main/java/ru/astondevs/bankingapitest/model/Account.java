package ru.astondevs.bankingapitest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import ru.astondevs.bankingapitest.exception.InsufficientBalanceException;
import ru.astondevs.bankingapitest.exception.InvalidPinException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс Account представляет собой модель банковского счета в банковском приложении.
 * Он содержит информацию о каждом счете, включая уникальный идентификатор, номер счета, имя владельца, PIN-код и баланс.
 */
@Entity
public class Account {

    /**
     * Уникальный идентификатор счета.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер счета.
     */
    private String accountNumber;

    /**
     * Имя владельца счета.
     */
    private String beneficiaryName;

    /**
     * PIN-код счета.
     */
    private String pin;

    /**
     * Баланс счета.
     */
    private BigDecimal balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    private List<Transaction> transactions = new ArrayList<>();

    protected Account() {
    }

    /**
     * Создает новый объект Account.
     *
     * @param beneficiaryName имя владельца счета
     * @param pin             PIN-код счета
     */
    public Account(String beneficiaryName, String pin) {
        this.accountNumber = UUID.randomUUID().toString();
        this.beneficiaryName = beneficiaryName;
        this.pin = pin;
        this.balance = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public String getPin() {
        return pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Вносит указанную сумму на счет.
     *
     * @param amount сумма для внесения
     */
    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    /**
     * Метод для снятия средств со счета.
     *
     * @param pin    PIN-код для проверки
     * @param amount сумма, которую нужно снять
     * @throws InvalidPinException          если введенный PIN-код не совпадает с PIN-кодом на счете
     * @throws InsufficientBalanceException если на счете недостаточно средств для снятия
     */
    public void withdraw(String pin, BigDecimal amount) {
        if (this.pin.equals(pin)) {
            if (this.balance.compareTo(amount) >= 0) {
                this.balance = this.balance.subtract(amount);
            } else {
                throw new InsufficientBalanceException("Insufficient balance");
            }
        } else {
            throw new InvalidPinException("Invalid PIN");
        }
    }

    /**
     * Метод для перевода средств с одного счета на другой.
     *
     * @param pin       PIN-код для проверки
     * @param amount    сумма, которую нужно перевести
     * @param toAccount счет, на который будут переведены средства
     * @throws InvalidPinException          если введенный PIN-код не совпадает с PIN-кодом на счете
     * @throws InsufficientBalanceException если на счете недостаточно средств для перевода
     */
    public void transfer(String pin, BigDecimal amount, Account toAccount) {
        if (this.pin.equals(pin)) {
            if (this.balance.compareTo(amount) >= 0) {
                this.balance = this.balance.subtract(amount);
                toAccount.balance = toAccount.balance.add(amount);
            } else {
                throw new InsufficientBalanceException("Insufficient balance");
            }
        } else {
            throw new InvalidPinException("Invalid PIN");
        }
    }
}
