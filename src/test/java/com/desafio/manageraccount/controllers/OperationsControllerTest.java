package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.builder.AccountDTOBuilder;
import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.desafio.manageraccount.services.OperationsServices;
import com.desafio.manageraccount.services.exceptions.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.desafio.manageraccount.utils.JsonConvertUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OperationsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OperationsServices operationsServices;

    @InjectMocks
    private OperationsController operationsController;

    private static final AccountDTO accountDTO = AccountDTOBuilder.builder().build().accountDTO();
    private final Operations operation = new Operations(1L,
            TypeOperations.DEPOSIT,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            100.0, TypeStatus.CONCLUDED, 0.0, null, null, null, null);

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(operationsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenGETListAllOperations() throws Exception {

        when(operationsServices.operationsList()).thenReturn(Collections.singletonList(operation));

        mockMvc.perform(get("/operations/all").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount", is(operation.getAmount())))
                .andExpect(jsonPath("$[0].dateOperation", is(operation.getDateOperation())))
                .andExpect(jsonPath("$[0].typeOperations", is(String.valueOf(operation.getTypeOperations()))))
                .andExpect(jsonPath("$[0].typeStatus", is(String.valueOf(operation.getTypeStatus()))));
    }

    @Test
    void whenGETBankStatementButAccountIsNotExist() throws Exception {

        doThrow(AccountNotFoundException.class).when(operationsServices).statement(accountDTO.getId());

        mockMvc.perform(get("/operations/statement/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETBankStatementForAccount() throws Exception {

        when(operationsServices.statement(accountDTO.getId())).thenReturn(Collections.singletonList(operation));

        mockMvc.perform(get("/operations/statement/?id=" + accountDTO.getId()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount", is(operation.getAmount())))
                .andExpect(jsonPath("$[0].dateOperation", is(operation.getDateOperation())))
                .andExpect(jsonPath("$[0].typeOperations", is(String.valueOf(operation.getTypeOperations()))))
                .andExpect(jsonPath("$[0].typeStatus", is(String.valueOf(operation.getTypeStatus()))));
    }

    @Test
    void whenGETTheOperationButIsNotExist() throws Exception {

        doThrow(BankingOperationsNotFound.class).when(operationsServices).operationById(operation.getIdOperation());

        mockMvc.perform(get("/operations/?id=" + operation.getIdOperation()).contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETTheOperation() throws Exception {

        when(operationsServices.operationById(operation.getIdOperation())).thenReturn(operation);

        mockMvc.perform(get("/operations/?id=" + operation.getIdOperation()).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(operation.getAmount())))
                .andExpect(jsonPath("$.dateOperation", is(operation.getDateOperation())))
                .andExpect(jsonPath("$.typeOperations", is(String.valueOf(operation.getTypeOperations()))))
                .andExpect(jsonPath("$.typeStatus", is(String.valueOf(operation.getTypeStatus()))));
    }

    @Test
    void whenPOSTOperationButAccountIsNoExist() throws Exception {

        doThrow(AccountNotFoundException.class).when(operationsServices).withdraw(accountDTO.getId(),operation);
        doThrow(AccountNotFoundException.class).when(operationsServices).deposit(accountDTO.getId(),operation);
        doThrow(AccountNotFoundException.class).when(operationsServices).bankTransfer(accountDTO.getId(),operation);

        mockMvc.perform(post("/operations/withdraw/?id=" + accountDTO.getId()).contentType("application/json")
                .content(asJsonString(operation)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/operations/deposit/?id=" + accountDTO.getId()).contentType("application/json")
                .content(asJsonString(operation)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/operations/transfer/?id=" + accountDTO.getId()).contentType("application/json")
                .content(asJsonString(operation)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPOSTOperationButAmountIsInvalid() throws Exception {

        doThrow(InvalidOperationExceptions.class).when(operationsServices).withdraw(accountDTO.getId(),operation);
        doThrow(InvalidOperationExceptions.class).when(operationsServices).deposit(accountDTO.getId(),operation);
        doThrow(InvalidOperationExceptions.class).when(operationsServices).bankTransfer(accountDTO.getId(),operation);

        mockMvc.perform(post("/operations/withdraw/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/operations/deposit/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/operations/transfer/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPOSTWithdrawOperationButKafkaIsOff() throws Exception {

        doThrow(CouldNotCompleteTheRequest.class).when(operationsServices).withdraw(accountDTO.getId(),operation);

        mockMvc.perform(post("/operations/withdraw/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isServiceUnavailable());
    }


    @Test
    void whenPOSTWithdrawOperationWithSuccessfully() throws Exception {
        Operations operationTest = operation;
        operationTest.setTypeOperations(TypeOperations.WITHDRAW);

        when(operationsServices.withdraw(accountDTO.getId(),operation)).thenReturn(operationTest);

        mockMvc.perform(post("/operations/withdraw/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(operationTest.getAmount())))
                .andExpect(jsonPath("$.dateOperation", is(operationTest.getDateOperation())))
                .andExpect(jsonPath("$.typeOperations", is(String.valueOf(operationTest.getTypeOperations()))))
                .andExpect(jsonPath("$.typeStatus", is(String.valueOf(operationTest.getTypeStatus()))))
                .andExpect(jsonPath("$.tax", is(operationTest.getTax())));
    }

    @Test
    void whenPOSTDepositOperationWithSuccessfully() throws Exception {
        Operations operationTest = operation;
        operationTest.setTypeOperations(TypeOperations.DEPOSIT);

        when(operationsServices.deposit(accountDTO.getId(),operation)).thenReturn(operationTest);

        mockMvc.perform(post("/operations/deposit/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(operationTest.getAmount())))
                .andExpect(jsonPath("$.dateOperation", is(operationTest.getDateOperation())))
                .andExpect(jsonPath("$.typeOperations", is(String.valueOf(operationTest.getTypeOperations()))))
                .andExpect(jsonPath("$.typeStatus", is(String.valueOf(operationTest.getTypeStatus()))));
    }

    @Test
    void whenPOSTBankTransferOperationButAccountDestinyIsInvalid() throws Exception {

        doThrow(DocumentationException.class).when(operationsServices).bankTransfer(accountDTO.getId(),operation);

        mockMvc.perform(post("/operations/transfer/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenPOSTBankTransferOperationWithSuccessfully() throws Exception {
        Operations operationTest = operation;
        operationTest.setTypeOperations(TypeOperations.BANKTRANSFER);
        operationTest.setAgencyDestiny("2222");
        operationTest.setAccountDestiny("55555");
        operationTest.setDestinyVerifyDigit("1");

        when(operationsServices.bankTransfer(accountDTO.getId(),operation)).thenReturn(operationTest);

        mockMvc.perform(post("/operations/transfer/?id=" + accountDTO.getId()).contentType("application/json")
                        .content(asJsonString(operation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(operationTest.getAmount())))
                .andExpect(jsonPath("$.dateOperation", is(operationTest.getDateOperation())))
                .andExpect(jsonPath("$.typeOperations", is(String.valueOf(operationTest.getTypeOperations()))))
                .andExpect(jsonPath("$.typeStatus", is(String.valueOf(operationTest.getTypeStatus()))))
                .andExpect(jsonPath("$.accountDestiny", is(String.valueOf(operationTest.getAccountDestiny()))))
                .andExpect(jsonPath("$.agencyDestiny", is(String.valueOf(operationTest.getAgencyDestiny()))))
                .andExpect(jsonPath("$.destinyVerifyDigit", is(String.valueOf(operationTest.getDestinyVerifyDigit()))));
    }


}
