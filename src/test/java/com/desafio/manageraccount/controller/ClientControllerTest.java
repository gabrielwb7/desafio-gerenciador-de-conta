package com.desafio.manageraccount.controller;

import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.controllers.ClientController;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.services.ClientService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Gson gson;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private static final ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();

    @Test
    public void clientListTest() throws Exception {

        when(clientService.listAllClients()).thenReturn(Collections.singletonList(clientDTO.toDTO()));

        mockMvc.perform(get("/clients/all").contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void insertClientTest() throws Exception {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();

        mockMvc.perform(post("/clients").contentType("application/json").content(gson.toJson(clientDTO))).andExpect(status().isCreated());
    }

}
