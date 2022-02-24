package com.desafio.manageraccount.services;

import com.desafio.manageraccount.builder.AccountDTOBuilder;
import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.ClientRepository;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

//    @Mock
//    private KafkaProducer<String,String> producer;

    @Mock
    private ProduceMessage<String> producer;

    @Mock
    private Jedis jedis;

    @InjectMocks
    private AccountService accountService;

    private static final long INVALID_ACCOUNT_ID = 1L;
    private static final ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
    private static final AccountDTO accountDTO = AccountDTOBuilder.builder().build().accountDTO();

    @Test
    void whenListAccountsIsCalledThenReturnAnEmptyListOfClients() {

        when(accountRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<Account> foundListAccount = accountService.listAllAccounts();

        assertThat(foundListAccount, Matchers.is((empty())));
    }

    @Test
    void whenListAccountsIsCalledThenReturnListOfClients() {

        when(accountRepository.findAll()).thenReturn(Collections.singletonList(accountDTO.toDTO()));
        List<Account> foundListAccount = accountService.listAllAccounts();

        assertThat(foundListAccount, Matchers.is(Matchers.not(empty())));
        assertThat(foundListAccount.get(0), Matchers.is(Matchers.equalTo(accountDTO.toDTO())));
    }

    @Test
    void whenCreatingTheAccountButTheClientDoesNotExist() {
        assertThrows(ClientNotFoundException.class, () -> accountService.insertAccount(accountDTO,clientDTO.getId()));
    }

    @Test
    void whenClientDoesntHaveCPFForCreateAccountRegularPerson() {
        ClientDTO clientTempDTO = new ClientDTO(1L,"Gabriel","11222224444","teste",null,  "59.144.503/0001-83");
        Client expectedClient = clientTempDTO.toDTO();

        when(clientRepository.findById(clientTempDTO.getId())).thenReturn(Optional.ofNullable(expectedClient));

        AccountDTO account = new AccountDTO(1L,TypeAccount.REGULARPERSON,"2211");

        when(accountRepository.save(account.toDTO())).thenReturn(null);

        assertThrows(DocumentationException.class, () -> accountService.insertAccount(account,clientDTO.getId()));
    }

    @Test
    void whenClientDoesntHaveCNPJForCreateAccountRegularPerson() {
        ClientDTO clientTempDTO = new ClientDTO("Gabriel","11222224444","teste","126.251.926-82",  null);
        Client expectedClient = clientTempDTO.toDTO();

        when(clientRepository.findById(clientTempDTO.getId())).thenReturn(Optional.ofNullable(expectedClient));

        AccountDTO account = new AccountDTO(1L,TypeAccount.LEGALPERSON,"2211");

        when(accountRepository.save(account.toDTO())).thenReturn(null);

        assertThrows(DocumentationException.class, () -> accountService.insertAccount(account,clientTempDTO.getId()));
    }

    @Test
    void whenInformedAAgencyInvalidForCreateAccount() {
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findById(clientDTO.getId())).thenReturn(Optional.ofNullable(expectedClient));

        AccountDTO account = new AccountDTO(1L,TypeAccount.REGULARPERSON,"22f1");

        when(accountRepository.save(account.toDTO())).thenReturn(null);

        assertThrows(DocumentationException.class, () -> accountService.insertAccount(account,clientDTO.getId()));
    }

    @Test
    void whenTheAccountIsCreatedWithSuccessfully() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        Client client = clientDTO.toDTO();

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);

        Account expectedAccount = accountService.insertAccount(accountDTO, client.getId());

        assertEquals(accountDTO.getTypeAccount(), expectedAccount.getTypeAccount());
        assertEquals(accountDTO.getAgency(), expectedAccount.getAgency());
        assertEquals(accountDTO.getTypeAccount().getMaxLimitWithdrawals(), expectedAccount.getQuantityWithdraw());
    }

//    @Test
//    void whenCreatingAccountButKafkaIsOf() throws ExecutionException, InterruptedException, TimeoutException, IOException {
//        Client client = clientDTO.toDTO();
//
//        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
//        when(clientRepository.save(client)).thenReturn(client);
//
//        producer.close();
//
//        assertThrows(CouldNotCompleteTheRequest.class, () -> accountService.insertAccount(accountDTO, client.getId()));
//    }

    @Test
    void whenDeleteClientWithSuccess() {

        Account expectedDeleteAccount = accountDTO.toDTO();

        when(accountRepository.findById(accountDTO.getId())).thenReturn(Optional.ofNullable(expectedDeleteAccount));
        doNothing().when(clientRepository).deleteById(clientDTO.getId());

        accountService.delete(accountDTO.getId());

        verify(accountRepository, times(1)).findById(accountDTO.getId());
        verify(accountRepository, times(1)).deleteById(accountDTO.getId());
    }

    @Test
    void whenDeleteAccountByIdIsNotExist() {
        when(accountRepository.findById(INVALID_ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.delete(INVALID_ACCOUNT_ID));
    }

    @Test
    void whenUpdateAccountWithSucess() {
        Account account = accountDTO.toDTO();
        AccountDTO accountUpdate = new AccountDTO(1L,TypeAccount.REGULARPERSON,"2222");

        when(accountRepository.findById(accountDTO.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Account afterUpdate = accountService.updateAccount(accountUpdate);

        assertEquals(afterUpdate.getAgency(), accountUpdate.getAgency());
    }

    @Test
    void whenInformedAAgencyInvalidForUpdateAccount() {
        Account account = accountDTO.toDTO();
        AccountDTO accountUpdate = new AccountDTO(1L,TypeAccount.REGULARPERSON,"22f1");

        when(accountRepository.findById(accountDTO.getId())).thenReturn(Optional.of(account));

        assertThrows(DocumentationException.class, () -> accountService.updateAccount(accountUpdate));
    }

//    @Test
//    void whenConsultWithdrawalsFreeWithSuccessfully() {
//        Account account = accountDTO.toDTO();
//
//        when(accountRepository.save(account)).thenReturn(account);
//        when(accountRepository.findById(accountDTO.getId())).thenReturn(Optional.of(account));
//
//        when(jedis.get(Long.toString(accountDTO.getId()))).thenReturn("5");
//
//        Account expectedAccount = accountService.consultWithdrawFree(accountDTO.getId());
//
//        assertEquals(5, expectedAccount.getQuantityWithdraw());
//    }


    @Test
    void whenInformedInvalidIDAccountForConsultWithdrawFree() {
        Account account = accountDTO.toDTO();

        when(accountRepository.save(account)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> accountService.consultWithdrawFree(account.getId()));
    }

    @Test
    void whenReturnAccountForIDWithSucess() {
        Account account = accountDTO.toDTO();

        when(accountRepository.findById(accountDTO.getId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        assertEquals(accountService.accountById(accountDTO.getId()), account);
    }





























}
