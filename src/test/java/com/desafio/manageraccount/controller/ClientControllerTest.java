package com.desafio.manageraccount.controller;

import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    public void clientListTest() throws Exception {
        mockMvc.perform(get("/clients/all")).andExpect(status().isOk());
    }

    @Test
    public void insertClientTest() throws Exception {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();

        mockMvc.perform(post("/clients").contentType("application/json").content(gson.toJson(clientDTO))).andExpect(status().isCreated());
    }

}
