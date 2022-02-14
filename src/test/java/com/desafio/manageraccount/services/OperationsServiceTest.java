package com.desafio.manageraccount.services;

import com.desafio.manageraccount.builder.AccountDTOBuilder;
import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.Tax;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.ClientRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OperationsServiceTest {

    @Mock
    private OperationsRepository operationsRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private OperationsServices operationsServices;

    @InjectMocks
    private AccountService accountService;

    private static final long INVALID_ACCOUNT_ID = 1L;
    private static final ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
    private static final AccountDTO accountDTO = AccountDTOBuilder.builder().build().accountDTO();
    private final Tax tax = new Tax();
    private final Operations operation = new Operations(1L,
            TypeOperations.DEPOSIT,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            100.0, TypeStatus.CONCLUDED, 0.0, null, null, null, null);

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

//    @Test
//    void whenTheBankStatemenIsMadeSuccessfully() {
//        Account account = new Account(1L,"2222","11111","1", TypeAccount.REGULARPERSON);
//
//        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
//        when(accountRepository.save(account)).thenReturn(account);
//        when(operationsRepository.findAll()).thenReturn(Collections.singletonList(operation));
//
//        Operations operationTest = operationsServices.deposit(account.getId(),operation);
//
//        when(operationsRepository.save(operationTest)).thenReturn(operationTest);
//
//        List<Operations> operationsList = operationsServices.statement(account.getId());
//
//        assertThat(operationsList, Matchers.is(Matchers.not(empty())));
//        assertThat(operationsList.get(0), Matchers.is(Matchers.equalTo(operation)));
//
//    }

    @Test
    void whenTheIdOperationInformedIsNoExist() {

        when(operationsRepository.save(operation)).thenReturn(null);

        assertThrows(BankingOperationsNotFound.class, () -> operationsServices.operationById(operation.getIdOperation()));
    }

    @Test
    void whenInformedInvalidIDAccountForOperation() {
        Account account = accountDTO.toDTO();

        when(accountRepository.save(account)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> operationsServices.deposit(account.getId(), operation));
        assertThrows(AccountNotFoundException.class, () -> operationsServices.bankTransfer(account.getId(), operation));
        assertThrows(AccountNotFoundException.class, () -> operationsServices.withdraw(account.getId(), operation));
    }

    @Test
    void whenInformedInvalidAmountForOperation() {
        Account account = accountDTO.toDTO();
        Operations operationInvalidAmount = new Operations(1L,
                TypeOperations.DEPOSIT,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                -100.0, TypeStatus.CONCLUDED, 0.0, null, null, null, null);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.deposit(account.getId(), operationInvalidAmount));
        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.bankTransfer(account.getId(), operationInvalidAmount));
        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.withdraw(account.getId(), operationInvalidAmount));
    }

    @Test
    void whenThereIsNotEnoughBalance() {
        Account account = accountDTO.toDTO();

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.bankTransfer(account.getId(), operation));
        assertThrows(InvalidOperationExceptions.class, () -> operationsServices.withdraw(account.getId(), operation));
    }

    @Test
    void whenInformedDataAccountDestinyIsInvalid() {
        Account account = accountDTO.toDTO();
        Operations operationInvalidData = new Operations(1L,
                TypeOperations.DEPOSIT,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                100.0, TypeStatus.CONCLUDED, 0.0, "22f22", "1444", "1", null);

        account.setBalanceAccount(150.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        assertThrows(DocumentationException.class, () -> operationsServices.bankTransfer(account.getId(), operationInvalidData));
    }

    @Test
    void whenInformedAccountDestinyIsNotExist() {
        Account account = accountDTO.toDTO();
        Operations operationInvalidData = new Operations(1L,
                TypeOperations.DEPOSIT,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                100.0, TypeStatus.CONCLUDED, 0.0, "22222", "1444", "1", null);

        account.setBalanceAccount(150.0);

        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        assertThrows(AccountNotFoundException.class, () -> operationsServices.bankTransfer(account.getId(), operationInvalidData));
    }


























}
