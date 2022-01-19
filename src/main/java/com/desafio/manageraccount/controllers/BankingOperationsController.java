package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.entities.BankingOperations;
import com.desafio.manageraccount.services.BankingOperationsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/operations")
public class BankingOperationsController {

    @Autowired
    private BankingOperationsServices bankingOperationsServices;

    @GetMapping
    public ResponseEntity<List<BankingOperations>> operationsList() {
        return ResponseEntity.ok().body(bankingOperationsServices.operationsList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BankingOperations> operationById(@PathVariable Long id) {
        return ResponseEntity.ok().body(bankingOperationsServices.operationById(id));
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<BankingOperations> insertOperation(@RequestBody BankingOperations operation, @PathVariable Long id) {
       BankingOperations newOperation = bankingOperationsServices.insertOperation(operation, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newOperation.getIdOperation()).toUri();
        return ResponseEntity.created(uri).body(newOperation);
    }

}
