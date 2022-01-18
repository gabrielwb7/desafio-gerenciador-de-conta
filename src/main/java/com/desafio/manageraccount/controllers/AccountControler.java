package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.dto.response.MessageResponse;
import com.desafio.manageraccount.exceptions.AccountAlreadyRegisteredException;
import com.desafio.manageraccount.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/accounts")
public class AccountControler {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> accountList() {
        return accountService.listAllAccounts();
    }

    @GetMapping(value = "/{id}")
    public Account accountById(@PathVariable Long id) throws AccountNotFoundException {
        return accountService.accountById(id);
    }

    @GetMapping(value = "/balance/{id}")
    public MessageResponse consultBalance(@PathVariable Long id) throws AccountNotFoundException {
        return accountService.consultBalance(id);
    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse insertAccount(@RequestBody AccountDTO accountDTO, @PathVariable Long id) throws AccountAlreadyRegisteredException {
        return accountService.insertAccount(accountDTO, id);
    }

    @PutMapping("/{id}")
    public MessageResponse updateAccount(@PathVariable Long id, @RequestBody AccountDTO accountDTO) throws AccountNotFoundException, AccountAlreadyRegisteredException {
        return accountService.updateAccount(id, accountDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) throws AccountNotFoundException {
        accountService.delete(id);
    }

//    @GetMapping(value = "/accounts-client/{id}")
//    public List<Account> accountsPerCustomer(@PathVariable Long id) {
//        return accountService.accountsPerClient(id);
//    }
}
