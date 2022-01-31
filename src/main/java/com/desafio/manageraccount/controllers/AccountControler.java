package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.dto.response.AccountResponseDTO;
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

    @GetMapping(value = "/all")
    public ResponseEntity<List<Account>> accountList() {
        return ResponseEntity.ok().body(accountService.listAllAccounts());
    }

    @GetMapping(value = "/withdraw")
    public ResponseEntity<AccountResponseDTO> withdrawFree (@RequestParam Long id) {
        return ResponseEntity.ok().body(AccountResponseDTO.toDTO(accountService.consultWithdrawFree(id)));
    }

    @GetMapping
    public ResponseEntity<Account> accountById(@RequestParam Long id) {
        return ResponseEntity.ok().body(accountService.accountById(id));
    }

    @GetMapping(value = "/balance/")
    public ResponseEntity<AccountResponseDTO> consultBalance(@RequestParam Long id) {
        return ResponseEntity.ok().body(AccountResponseDTO.toDTO(accountService.consultBalance(id)));
    }

    @PostMapping
    public ResponseEntity<Account> insertAccount(@RequestBody @Valid AccountDTO accountDTO, @RequestParam Long id) {
        Account account = accountService.insertAccount(accountDTO, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(account.getId()).toUri();
        return ResponseEntity.created(uri).body(account);
    }

    @PutMapping
    public ResponseEntity<Account> updateAccount(@RequestParam Long id, @RequestBody @Valid AccountDTO accountDTO) {
        return ResponseEntity.ok().body(accountService.updateAccount(id, accountDTO));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@RequestParam Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
