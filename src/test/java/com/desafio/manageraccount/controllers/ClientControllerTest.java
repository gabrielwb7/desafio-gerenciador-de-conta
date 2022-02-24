package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.services.ClientService;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
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
public class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    private static final ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();

    @Test
    public void clientListTest() throws Exception {

        when(clientService.listAllClients()).thenReturn(Collections.singletonList(clientDTO.toDTO()));

        mockMvc.perform(get("/clients/all").contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGETListAllClients() throws Exception {

        when(clientService.listAllClients()).thenReturn(Collections.singletonList(clientDTO.toDTO()));

        mockMvc.perform(get("/clients/all").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(clientDTO.getName())))
                .andExpect(jsonPath("$[0].clientCPF", is(clientDTO.getClientCPF())))
                .andExpect(jsonPath("$[0].clientCNPJ", is(clientDTO.getClientCNPJ())))
                .andExpect(jsonPath("$[0].address", is(clientDTO.getAddress())))
                .andExpect(jsonPath("$[0].phoneNumber", is(clientDTO.getPhoneNumber())));
    }

    @Test
    public void whenGETClientByCPF() throws Exception {

        when(clientService.clientByCPF(clientDTO)).thenReturn(clientDTO.toDTO());

        mockMvc.perform(get("/clients/cpf").contentType("application/json").content(asJsonString(clientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(clientDTO.getName())))
                .andExpect(jsonPath("$.clientCPF", is(clientDTO.getClientCPF())))
                .andExpect(jsonPath("$.clientCNPJ", is(clientDTO.getClientCNPJ())))
                .andExpect(jsonPath("$.address", is(clientDTO.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(clientDTO.getPhoneNumber())));
    }

    @Test
    public void whenGETClientByCPFButNotFound() throws Exception {

        when(clientService.clientByCPF(clientDTO)).thenThrow(ClientNotFoundException.class);

        mockMvc.perform(get("/clients/cpf").contentType("application/json").content(asJsonString(clientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGETClientByCNPJ() throws Exception {

        when(clientService.clientByCNPJ(clientDTO)).thenReturn(clientDTO.toDTO());

        mockMvc.perform(get("/clients/cnpj").contentType("application/json").content(asJsonString(clientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(clientDTO.getName())))
                .andExpect(jsonPath("$.clientCPF", is(clientDTO.getClientCPF())))
                .andExpect(jsonPath("$.clientCNPJ", is(clientDTO.getClientCNPJ())))
                .andExpect(jsonPath("$.address", is(clientDTO.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(clientDTO.getPhoneNumber())));
    }

    @Test
    public void whenGETClientByCNPJFButNotFound() throws Exception {

        when(clientService.clientByCNPJ(clientDTO)).thenThrow(ClientNotFoundException.class);

        mockMvc.perform(get("/clients/cnpj").contentType("application/json").content(asJsonString(clientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenPOSTIsCreatedClient() throws Exception {

        when(clientService.insertClient(clientDTO)).thenReturn(clientDTO.toDTO());

        mockMvc.perform(post("/clients").contentType("application/json").content(asJsonString(clientDTO))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(clientDTO.getName())))
                .andExpect(jsonPath("$.clientCPF", is(clientDTO.getClientCPF())))
                .andExpect(jsonPath("$.clientCNPJ", is(clientDTO.getClientCNPJ())))
                .andExpect(jsonPath("$.address", is(clientDTO.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(clientDTO.getPhoneNumber())));
    }

    @Test
    public void whenPOSTIsCreatedClientButNoInformedDocument() throws Exception {
        ClientDTO clientDTOTest = new ClientDTO("Gabriel","22999995555","rua teste",null,  null);

        when(clientService.insertClient(clientDTOTest)).thenThrow(DocumentationException.class);

        mockMvc.perform(post("/clients").contentType("application/json").content(asJsonString(clientDTOTest))).andExpect(status().isBadRequest());

    }

    @Test
    public void whenPOSTIsCreatedClientButInformedInvalidPhoneNumber() throws Exception {
        ClientDTO clientDTOTest = new ClientDTO("Gabriel","229999af5555","rua teste","938.447.860-15",  "01.120.328/0001-04");

        when(clientService.insertClient(clientDTOTest)).thenThrow(DocumentationException.class);

        mockMvc.perform(post("/clients").contentType("application/json").content(asJsonString(clientDTOTest))).andExpect(status().isBadRequest());

    }

    @Test
    void whenThePUTButNoDocumentWasInformed() throws Exception {
        ClientDTO clientDTOTest = new ClientDTO("Gabriel","22999995555","rua teste",null,  null);

        doThrow(DocumentationException.class).when(clientService).updateClient(clientDTOTest);

        mockMvc.perform(put("/clients").contentType("application/json").content(asJsonString(clientDTOTest))).andExpect(status().isBadRequest());
    }

    @Test
    void whenThePUTButInformedInvalidPhoneNumber() throws Exception {

        ClientDTO clientDTOTest = new ClientDTO("Gabriel","229999af5555","rua teste","938.447.860-15",  "01.120.328/0001-04");

        doThrow(DocumentationException.class).when(clientService).updateClient(clientDTOTest);

        mockMvc.perform(put("/clients").contentType("application/json").content(asJsonString(clientDTOTest))).andExpect(status().isBadRequest());
    }

    @Test
    void whenThePUTButClientIsNoExist() throws Exception {

        doThrow(ClientNotFoundException.class).when(clientService).updateClient(clientDTO);

        mockMvc.perform(put("/clients").contentType("application/json").content(asJsonString(clientDTO))).andExpect(status().isNotFound());
    }


    @Test
    void whenThePUTisDoneSuccesfully() throws Exception {
        ClientDTO newData = new ClientDTO("Gabriel","22999995555","rua teste","938.447.860-15",  "01.120.328/0001-04");

        when(clientService.updateClient(newData)).thenReturn(newData.toDTO());

        mockMvc.perform(put("/clients").contentType("application/json").content(asJsonString(newData))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newData.getName())))
                .andExpect(jsonPath("$.clientCPF", is(newData.getClientCPF())))
                .andExpect(jsonPath("$.clientCNPJ", is(newData.getClientCNPJ())))
                .andExpect(jsonPath("$.address", is(newData.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(newData.getPhoneNumber())));
    }

    @Test
    void whenTheDELETEisDoneInvalidId() throws Exception {

        doThrow(ClientNotFoundException.class).when(clientService).deleteClientById(clientDTO.getId());

        mockMvc.perform(delete("/clients/?id=" + clientDTO.getId())).andExpect(status().isNotFound());
    }

    @Test
    void whenTheDELETEisDoneSuccessfully() throws Exception {

        doNothing().when(clientService).deleteClientById(clientDTO.getId());

        mockMvc.perform(delete("/clients/?id=" + clientDTO.getId()).contentType("application/json")).andExpect(status().isNoContent());
    }










}
