package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/accounts")
public class AccountControler {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> accountList() {
        return ResponseEntity.ok().body(accountService.listAllAccounts());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Account> accountById(@PathVariable Long id) {
        return ResponseEntity.ok().body(accountService.accountById(id));
    }

    @GetMapping(value = "/balance/{id}")
    public ResponseEntity<String> consultBalance(@PathVariable Long id) {
        return ResponseEntity.ok().body(accountService.consultBalance(id));
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Account> insertAccount(@RequestBody @Valid AccountDTO accountDTO, @PathVariable Long id) {
        Account account = accountService.insertAccount(accountDTO, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(account.getId()).toUri();
        return ResponseEntity.created(uri).body(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDTO accountDTO) {
        return ResponseEntity.ok().body(accountService.updateAccount(id, accountDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
