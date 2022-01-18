package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.entities.BankingOperations;
import com.desafio.manageraccount.dto.response.MessageResponse;
import com.desafio.manageraccount.services.BankingOperationsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/operations")
public class BankingOperationsController {

    @Autowired
    private BankingOperationsServices bankingOperationsServices;

    @GetMapping
    public List<BankingOperations> clientList() {
        return bankingOperationsServices.operationsList();
    }

    @GetMapping(value = "/{id}")
    public BankingOperations operationById(@PathVariable Long id) {
        return bankingOperationsServices.operationById(id);
    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse insertOperation(@RequestBody BankingOperations operation, @PathVariable Long id) {
        return bankingOperationsServices.insertOperation(operation, id);
    }

}
