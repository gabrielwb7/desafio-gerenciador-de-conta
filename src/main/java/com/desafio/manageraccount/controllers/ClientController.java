package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.response.MessageResponse;
import com.desafio.manageraccount.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<Client> clientList() {
        return clientService.listAllClients();
    }

    @GetMapping(value = "/{id}")
    public Client clientById(@PathVariable Long id) {
        return clientService.clientById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse insertClient(@RequestBody Client client) {
        return clientService.insertClient(client);
    }


    @PutMapping("/{id}")
    public MessageResponse updateClient(@PathVariable Long id, @RequestBody @Valid Client client) {
        return clientService.updateClient(id, client);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClientById(id);
    }
}
