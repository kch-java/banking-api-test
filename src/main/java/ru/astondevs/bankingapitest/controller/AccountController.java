package ru.astondevs.bankingapitest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.astondevs.bankingapitest.dto.AccountCreationRequest;
import ru.astondevs.bankingapitest.dto.AccountDto;
import ru.astondevs.bankingapitest.dto.DepositRequest;
import ru.astondevs.bankingapitest.dto.TransactionDto;
import ru.astondevs.bankingapitest.dto.TransferRequest;
import ru.astondevs.bankingapitest.dto.WithdrawRequest;
import ru.astondevs.bankingapitest.exception.InvalidRequestException;
import ru.astondevs.bankingapitest.mapper.AccountMapper;
import ru.astondevs.bankingapitest.mapper.TransactionMapper;
import ru.astondevs.bankingapitest.model.Account;
import ru.astondevs.bankingapitest.model.Transaction;
import ru.astondevs.bankingapitest.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс AccountController обрабатывает HTTP-запросы, связанные с операциями над счетами.
 * Он содержит методы для создания счетов, получения информации о счетах, внесения депозитов, снятия средств и перевода средств между счетами.
 */
@Tag(name = "Account Management", description = "Operations pertaining to account in Account Management")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper, TransactionMapper transactionMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Обрабатывает HTTP-запрос POST для создания нового счета.
     *
     * @param request объект AccountCreationRequest, содержащий информацию для создания нового счета.
     * @return ResponseEntity с информацией о созданном счете.
     */
    @Operation(summary = "Create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
    })
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @Parameter(description = "Account creation object", required = true) @RequestBody AccountCreationRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request body must not be null");
        }
        Account account = accountService.createAccount(request.getBeneficiaryName(), request.getPin());
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    /**
     * Обрабатывает HTTP-запрос GET для получения информации о счете.
     *
     * @param id идентификатор счета, информацию о котором нужно получить.
     * @return ResponseEntity с информацией о счете.
     */
    @Operation(summary = "Get an account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(
            @Parameter(description = "ID of the account to be obtained", required = true) @PathVariable Long id) {
        Account account = accountService.getAccount(id);
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    /**
     * Обрабатывает HTTP-запрос POST для внесения депозита на счет.
     *
     * @param id      идентификатор счета, на который нужно внести депозит.
     * @param request объект DepositRequest, содержащий информацию о депозите.
     * @return ResponseEntity с информацией о счете после внесения депозита.
     */
    @Operation(summary = "Deposit an amount to an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit made successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
    })
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(
            @Parameter(description = "ID of the account to deposit to", required = true) @PathVariable Long id,
            @Parameter(description = "Deposit request object", required = true) @RequestBody @Valid DepositRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request body must not be null");
        }
        Account account = accountService.deposit(id, request.getAmount());
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    /**
     * Обрабатывает HTTP-запрос POST для снятия средств со счета.
     *
     * @param id      идентификатор счета, с которого нужно снять средства.
     * @param request объект WithdrawRequest, содержащий информацию о снятии.
     * @return ResponseEntity с информацией о счете после снятия средств.
     */
    @Operation(summary = "Withdraw an amount from an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal made successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "402", description = "Payment Required"),
    })
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountDto> withdraw(
            @Parameter(description = "ID of the account to withdraw from", required = true) @PathVariable Long id,
            @Parameter(description = "Withdraw request object", required = true) @RequestBody @Valid WithdrawRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request body must not be null");
        }
        Account account = accountService.withdraw(id, request.getPin(), request.getAmount());
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    /**
     * Обрабатывает HTTP-запрос POST для перевода средств с одного счета на другой.
     *
     * @param id      идентификатор счета, с которого нужно перевести средства.
     * @param request объект TransferRequest, содержащий информацию о переводе.
     * @return ResponseEntity с информацией о счете после перевода средств.
     */
    @Operation(summary = "Transfer an amount from one account to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer made successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "402", description = "Payment Required"),
    })
    @PostMapping("/{id}/transfer")
    public ResponseEntity<AccountDto> transfer(
            @Parameter(description = "ID of the account to transfer from", required = true) @PathVariable Long id,
            @Parameter(description = "Transfer request object", required = true) @RequestBody @Valid TransferRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Request body must not be null");
        }
        Account account = accountService.transfer(id, request.getPin(), request.getAmount(), request.getToAccountId());
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    /**
     * Обрабатывает HTTP-запрос GET для получения списка всех транзакций для указанного счета.
     *
     * @param id идентификатор счета, транзакции которого нужно получить.
     * @return ResponseEntity со списком всех транзакций для указанного счета.
     */
    @Operation(summary = "Get transactions of an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
    })
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @Parameter(description = "ID of the account to get transactions from", required = true) @PathVariable Long id) {
        List<Transaction> transactions = accountService.getTransactions(id);
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    /**
     * Обрабатывает HTTP-запрос GET для получения всех счетов или их фильтрации по имени бенефициара.
     *
     * @param beneficiaryName Опциональный параметр: имя бенефициара для фильтрации счетов.
     * @return ResponseEntity со списком AccountDto. Если предоставлено имя бенефициара, возвращаются только счета этого бенефициара.
     */
    @Operation(summary = "Get all accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
    })
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts(
            @Parameter(description = "Optional: Beneficiary name to filter accounts")
            @RequestParam(required = false) String beneficiaryName) {
        List<Account> accounts;
        if (beneficiaryName != null) {
            accounts = accountService.getAllAccountsByBeneficiaryName(beneficiaryName);
        } else {
            accounts = accountService.getAllAccounts();
        }
        List<AccountDto> accountDtos = accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDtos);
    }
}
