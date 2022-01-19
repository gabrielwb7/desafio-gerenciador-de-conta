package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Client>> clientList() {
        return ResponseEntity.ok().body(clientService.listAllClients());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Client> clientById(@PathVariable Long id) {
        return ResponseEntity.ok().body(clientService.clientById(id));
    }

    @PostMapping
    public ResponseEntity<Client> insertClient(@RequestBody  @Valid ClientDTO clientDTO) {
        Client client = clientService.insertClient(clientDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(client.getId()).toUri();
        return ResponseEntity.created(uri).body(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient (@PathVariable Long id, @RequestBody @Valid ClientDTO clientDTO) {
        return ResponseEntity.ok().body(clientService.updateClient(id, clientDTO));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClientById(id);
        return ResponseEntity.noContent().build();
    }
}
