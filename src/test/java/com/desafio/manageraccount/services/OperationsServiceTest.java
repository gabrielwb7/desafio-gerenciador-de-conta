package com.desafio.manageraccount.services;

import com.desafio.manageraccount.builder.AccountDTOBuilder;
import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.OperationsRepository;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.BankingOperationsNotFound;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import com.desafio.manageraccount.services.exceptions.InvalidOperationExceptions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OperationsServiceTest {

    @Mock
    private OperationsRepository operationsRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private OperationsServices operationsServices;

    private final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost",6379);
    private final Jedis jedis = pool.getResource();

    private static final AccountDTO accountDTO = AccountDTOBuilder.builder().build().accountDTO();
    private final Operations operation = new Operations(1L,
            null,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            100.0, null, 0.0, null, null, null, null);

    @Test
    void whenListAccountsIsCalledThenReturnAnEmptyListOfOperations() {

        when(accountRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<Operations> foundListOperations = operationsServices.operationsList();

        assertThat(foundListOperations, Matchers.is((empty())));
    }

    @Test
    void whenListAccountsIsCalledThenReturnListOfOperations() {

        when(operationsRepository.findAll()).thenReturn(Collections.singletonList(operation));
        List<Operations> operationsList = operationsServices.operationsList();

        assertThat(operationsList, Matchers.is(Matchers.not(empty())));
        assertThat(operationsList.get(0), Matchers.is(Matchers.equalTo(operation)));
    }

    @Test
    void whenTheInformedAccountDoesNotExistForTheStatement() {
        Account account = accountDTO.toDTO();

        when(accountRepository.save(account)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> operationsServices.statement(account.getId()));
    }

    @Test
    void whenTheBankStatemenIsMadeSuccessfully() {
        Account account = new Account(1L,"2222","11111","1", TypeAccount.REGULARPERSON);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.getById(account.getId())).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(operationsRepository.findByAccountId(account.getId())).thenReturn(Collections.singletonList(operation));
        when(operationsRepository.findByAccountDestinyAndAgencyDestinyAndDestinyVerifyDigit("11111","2222", "1")).thenReturn(Collections.EMPTY_LIST);

        Operations operationTest = operationsServices.deposit(account.getId(),operation);

        List<Operations> operationsList = operationsServices.statement(account.getId());

        assertThat(operationsList, Matchers.is(Matchers.not(empty())));
        assertThat(operationsList.get(0), Matchers.is(Matchers.equalTo(operation)));

    }

    @Test
    void whenTheIdOperationInformedIsNoExist() {

        when(operationsRepository.save(operation)).thenReturn(null);

        assertThrows(BankingOperationsNotFound.class, () -> operationsServices.operationById(operation.getIdOperation()));
    }

    @Test
    void whenInformedInvalidIDAccountForOperation() {
        Account account = accountDTO.toDTO();

        when(accountRepository.findById(accountDTO.getId())).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> operationsServices.deposit(account.getId(), operation));
        assertThrows(AccountNotFoundException.class, () -> operationsServices.bankTransfer(account.getId(), operation));
        assertThrows(AccountNotFoundException.class, () -> operationsServices.withdraw(account.getId(), operation));
    }

    @Test
    void whenInformedInvalidAmountForOperation() {
        Account account = accountDTO.toDTO();
        Operations operationInvalidAmount = new Operations(1L,
                null,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                -100.0, null, 0.0, null, null, null, null);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.deposit(account.getId(), operationInvalidAmount));
        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.bankTransfer(account.getId(), operationInvalidAmount));
        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.withdraw(account.getId(), operationInvalidAmount));
    }

    @Test
    void whenThereIsNotEnoughBalance() {
        Account account = accountDTO.toDTO();

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.bankTransfer(account.getId(), operation));
        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.withdraw(account.getId(), operation));
    }

    @Test
    void whenDepositWithSucess() {
        Account account = accountDTO.toDTO();

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        operationsServices.deposit(account.getId(),operation);

        assertEquals(account.getBalanceAccount(), 100.0);
    }

    @Test
    void whenInformedDataAccountDestinyIsInvalid() {
        Account account = accountDTO.toDTO();
        Operations operationInvalidData = new Operations(1L,
                null,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                100.0, null, 0.0, "22f22", "1444", "1", null);

        account.setBalanceAccount(150.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        assertThrows(DocumentationException.class, () -> operationsServices.bankTransfer(account.getId(), operationInvalidData));
    }

    @Test
    void whenInformedAccountDestinyIsNotExist() {
        Account account = accountDTO.toDTO();
        Operations operationInvalidData = new Operations(1L,
               null,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                100.0, null, 0.0, "22222", "1444", "1", null);

        account.setBalanceAccount(150.0);

        assertThrows(AccountNotFoundException.class, () -> operationsServices.bankTransfer(account.getId(), operationInvalidData));
    }


    @Test
    void whenBankTransforWithSuccess() {
        Account account = accountDTO.toDTO();
        Account accountDestiny = new Account(2L, "1444", "22222", "1", TypeAccount.REGULARPERSON);
        Operations operationBankTransfer = new Operations(1L,
                null,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                100.0, null, 0.0, "22222", "1444", "1", null);

        account.setBalanceAccount(150.0);

        when(accountRepository.findById(accountDestiny.getId())).thenReturn(Optional.of(accountDestiny));

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.findByAgencyAndNumberAccountAndVerifyDigit("1444","22222", "1")).thenReturn(accountDestiny);

        operationsServices.bankTransfer(account.getId(),operationBankTransfer);

        assertEquals(account.getBalanceAccount(), 50.0);
        assertEquals(accountDestiny.getBalanceAccount(), 100.0);
    }

    @Test
    void whenTheWithdrawalIsMadeWithoutFee() {
        Account account = new Account(1L, "1444", "22222", "1", TypeAccount.REGULARPERSON);
        account.setBalanceAccount(150.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        jedis.set(Long.toString(1L), "5");
        operationsServices.withdraw(account.getId(), operation);

        assertEquals(50.0, account.getBalanceAccount());
    }

    @Test
    void whenTheWithdrawalIsMadeWithAFee() {
        Account account = new Account(1L, "1444", "22222", "1", TypeAccount.REGULARPERSON);
        account.setBalanceAccount(150.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        jedis.set(Long.toString(1L), "0");
        operationsServices.withdraw(account.getId(), operation);

        assertEquals(40.0, account.getBalanceAccount());
    }

    @Test
    void whenTheWithdrawalIsMadeWithAFeeButThereIsNoBalance() {
        Account account = new Account(1L, "1444", "22222", "1", TypeAccount.REGULARPERSON);
        account.setBalanceAccount(150.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        jedis.set(Long.toString(1L), "0");
        operation.setAmount(150.0);

        assertThrows(InvalidOperationExceptions.class, () ->  operationsServices.withdraw(account.getId(), operation));
    }




































}
