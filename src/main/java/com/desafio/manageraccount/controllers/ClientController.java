package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public List<Client> findAll() {
        List<Client> clientList = clientRepository.findAll();
        return clientList;
    }

    @GetMapping(value = "/{id}")
    public Client findById(@PathVariable Long id) {
        Client client = clientRepository.findById(id).get();
        return client;
    }

    @PostMapping
    public Client insertClient(@RequestBody Client client) {
        Client newCliente = clientRepository.save(client);
        return newCliente;
    }


}
