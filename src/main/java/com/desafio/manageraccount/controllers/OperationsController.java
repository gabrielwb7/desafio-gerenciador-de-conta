package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.dto.response.responsesoperations.ResponseDeposit;
import com.desafio.manageraccount.dto.response.responsesoperations.ResponseTransfer;
import com.desafio.manageraccount.dto.response.responsesoperations.ResponseWithdraw;
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

    @PostMapping("/withdraw")
    public ResponseEntity<ResponseWithdraw> withdraw(@RequestBody Operations operation, @RequestParam Long id) {
        Long idTemp = (long) (operationsServices.operationsList().size() + 1);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(idTemp).toUri();
        return ResponseEntity.created(uri).body(ResponseWithdraw.responseWithdraw(operationsServices.withdraw(id, operation)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ResponseDeposit> deposit(@RequestBody Operations operation, @RequestParam Long id) {
        Long idTemp = (long) (operationsServices.operationsList().size() + 1);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(idTemp).toUri();
        return ResponseEntity.created(uri).body(ResponseDeposit.responseDeposit(operationsServices.deposit(id, operation)));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ResponseTransfer> transfer(@RequestBody Operations operation, @RequestParam Long id) {
        Long idTemp = (long) (operationsServices.operationsList().size() + 1);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(idTemp).toUri();
        return ResponseEntity.created(uri).body(ResponseTransfer.responseTransfer(operationsServices.bankTransfer(id, operation)));
    }

}
