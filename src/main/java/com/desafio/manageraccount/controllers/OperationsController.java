package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.services.OperationsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/operations")
public class OperationsController {

    @Autowired
    private OperationsServices operationsServices;

    @GetMapping(value = "/all")
    public ResponseEntity<List<Operations>> operationsList() {
        return ResponseEntity.ok().body(operationsServices.operationsList());
    }

    @GetMapping(value = "/statement")
    public ResponseEntity<List<Operations>> operationsList(@RequestParam Long id) {
        return ResponseEntity.ok().body(operationsServices.statement(id));
    }

    @GetMapping
    public ResponseEntity<Operations> operationById(@RequestParam Long id) {
        return ResponseEntity.ok().body(operationsServices.operationById(id));
    }

    @PostMapping
    public ResponseEntity<Operations> insertOperation(@RequestBody Operations operation, @RequestParam Long id) {
       Operations newOperation = operationsServices.insertOperation(operation, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newOperation.getIdOperation()).toUri();
        return ResponseEntity.created(uri).body(newOperation);
    }

}
