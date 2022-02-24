package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.builder.AccountDTOBuilder;
import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.services.AccountService;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.exceptions.CouldNotCompleteTheRequest;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.desafio.manageraccount.utils.JsonConvertUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    private static final ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
    private static final AccountDTO accountDTO = AccountDTOBuilder.builder().build().accountDTO();

    @InjectMocks
    private AccountControler accountControler;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountControler)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenGETListAllAccounts() throws Exception {
        Account accountTest = accountDTO.toDTO();
        accountTest.setNumberAccount("5555");
        accountTest.setVerifyDigit("5");

        when(accountService.listAllAccounts()).thenReturn(Collections.singletonList(accountTest));

        mockMvc.perform(get("/accounts/all").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].agency", is(accountTest.getAgency())))
                .andExpect(jsonPath("$[0].numberAccount", is(accountTest.getNumberAccount())))
                .andExpect(jsonPath("$[0].verifyDigit", is(accountTest.getVerifyDigit())))
                .andExpect(jsonPath("$[0].typeAccount", is(String.valueOf(accountTest.getTypeAccount()))));
    }

    @Test
    void whenGETAccountByIdButIdNoExist() throws Exception {

        doThrow(AccountNotFoundException.class).when(accountService).accountById(accountDTO.getId());

        mockMvc.perform(get("/accounts/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETAccountById() throws Exception {
        Account accountTest = accountDTO.toDTO();
        accountTest.setNumberAccount("5555");
        accountTest.setVerifyDigit("5");

        when(accountService.accountById(accountDTO.getId())).thenReturn(accountTest);

        mockMvc.perform(get("/accounts/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agency", is(accountTest.getAgency())))
                .andExpect(jsonPath("$.numberAccount", is(accountTest.getNumberAccount())))
                .andExpect(jsonPath("$.verifyDigit", is(accountTest.getVerifyDigit())));
    }

    @Test
    void whenGETQuantityWithdrawsFreeButAccountIsNoExist() throws Exception {

        doThrow(AccountNotFoundException.class).when(accountService).consultWithdrawFree(accountDTO.getId());

        mockMvc.perform(get("/accounts/withdraw/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETQuantityWithdrawsFree() throws Exception {

        Account accountTest = accountDTO.toDTO();
        accountTest.setQuantityWithdraw(5);

        when(accountService.consultWithdrawFree(accountDTO.getId())).thenReturn(accountTest);

        mockMvc.perform(get("/accounts/withdraw/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.withdrawsFree", is(accountTest.getQuantityWithdraw())));

    }

    @Test
    void whenGETBalanceButAccountIsNoExist() throws Exception {

        doThrow(AccountNotFoundException.class).when(accountService).accountById(accountDTO.getId());

        mockMvc.perform(get("/accounts/balance/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETAccountBalance() throws Exception {

        Account accountTest = accountDTO.toDTO();
        accountTest.setBalanceAccount(20.0);

        when(accountService.accountById(accountDTO.getId())).thenReturn(accountTest);

        mockMvc.perform(get("/accounts/balance/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceAccount", is(accountTest.getBalanceAccount())));

    }

    @Test
    void whenPOSTTheAccountButClientIsNotExist() throws Exception {

        doThrow(ClientNotFoundException.class).when(accountService).insertAccount(accountDTO, clientDTO.getId());

        mockMvc.perform(post("/accounts?id=" + clientDTO.getId())
                .contentType("application/json").content(asJsonString(accountDTO))).andExpect(status().isNotFound());
    }

    @Test
    void whenPOSTTheAccountButClientHasNoDocument() throws Exception {

        doThrow(DocumentationException.class).when(accountService).insertAccount(accountDTO, clientDTO.getId());

        mockMvc.perform(post("/accounts?id=" + clientDTO.getId())
                .contentType("application/json").content(asJsonString(accountDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPOSTTheAccountButKafkaIsOff() throws Exception {

        doThrow(CouldNotCompleteTheRequest.class).when(accountService).insertAccount(accountDTO, clientDTO.getId());

        mockMvc.perform(post("/accounts?id=" + clientDTO.getId())
                .contentType("application/json").content(asJsonString(accountDTO))).andExpect(status().isServiceUnavailable());
    }

    @Test
    void whenPOSTTheAccountWithSuccessfully() throws Exception {
        Account accountTest = accountDTO.toDTO();
        accountTest.setNumberAccount("5555");
        accountTest.setVerifyDigit("5");

        when(accountService.insertAccount(accountDTO, clientDTO.getId())).thenReturn(accountTest);

        mockMvc.perform(post("/accounts?id=" + clientDTO.getId())
                .contentType("application/json").content(asJsonString(accountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agency", is(accountTest.getAgency())))
                .andExpect(jsonPath("$.numberAccount", is(accountTest.getNumberAccount())))
                .andExpect(jsonPath("$.verifyDigit", is(accountTest.getVerifyDigit())));
    }

    @Test
    void whenPUTheAccountButAccountNoExist() throws Exception {

        doThrow(AccountNotFoundException.class).when(accountService).updateAccount(accountDTO);

        mockMvc.perform(put("/accounts")
                .contentType("application/json").content(asJsonString(accountDTO))).andExpect(status().isNotFound());
    }

    @Test
    void whenPUTheAccountButAgencyInvalid() throws Exception {

        doThrow(DocumentationException.class).when(accountService).updateAccount(accountDTO);

        mockMvc.perform(put("/accounts")
                .contentType("application/json").content(asJsonString(accountDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPUTheAccountWithSuccessfully() throws Exception {
        Account accountTest = accountDTO.toDTO();
        accountTest.setNumberAccount("5555");
        accountTest.setVerifyDigit("5");

        when(accountService.updateAccount(accountDTO)).thenReturn(accountTest);

        mockMvc.perform(put("/accounts")
                        .contentType("application/json").content(asJsonString(accountDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agency", is(accountTest.getAgency())))
                .andExpect(jsonPath("$.numberAccount", is(accountTest.getNumberAccount())))
                .andExpect(jsonPath("$.verifyDigit", is(accountTest.getVerifyDigit())));
    }

    @Test
    void whenTheDELETEisDoneInvalidId() throws Exception {

        doThrow(AccountNotFoundException.class).when(accountService).delete(accountDTO.getId());

        mockMvc.perform(delete("/accounts/?id=" + accountDTO.getId())).andExpect(status().isNotFound());
    }

    @Test
    void whenTheDELETEisDoneSuccessfully() throws Exception {

        doNothing().when(accountService).delete(accountDTO.getId());

        mockMvc.perform(delete("/accounts/?id=" + clientDTO.getId()).contentType("application/json")).andExpect(status().isNoContent());
    }




























}
