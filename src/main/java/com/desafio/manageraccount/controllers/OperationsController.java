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

    @GetMapping
    public ResponseEntity<List<Operations>> operationsList() {
        return ResponseEntity.ok().body(operationsServices.operationsList());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Operations> operationById(@PathVariable Long id) {
        return ResponseEntity.ok().body(operationsServices.operationById(id));
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Operations> insertOperation(@RequestBody Operations operation, @PathVariable Long id) {
       Operations newOperation = operationsServices.insertOperation(operation, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newOperation.getIdOperation()).toUri();
        return ResponseEntity.created(uri).body(newOperation);
    }

}
