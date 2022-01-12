package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.entities.Account;
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
    public Account accountById(@PathVariable Long id) {
        return accountService.accountById(id);
    }

//    @GetMapping(value = "/accounts-client/{id}")
//    public List<Account> accountsPerCustomer(@PathVariable Long id) {
//        return accountService.accountsPerClient(id);
//    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Account insertAccount(@RequestBody Account account, @PathVariable Long id) {
        return accountService.insertAccount(account, id);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) throws AccountNotFoundException {
        return accountService.updateAccount(id, account);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) throws AccountNotFoundException {
        accountService.delete(id);
    }
}
