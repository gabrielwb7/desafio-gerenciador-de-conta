package com.desafio.manageraccount.controllers;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.dto.response.responsesaccount.AccountResponseDTO;
import com.desafio.manageraccount.dto.response.responsesaccount.BalanceResponse;
import com.desafio.manageraccount.dto.response.responsesaccount.WithdrawalsFreeResponse;
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

    @GetMapping
    public ResponseEntity<AccountResponseDTO> accountById(@RequestParam Long id) {
        return ResponseEntity.ok().body(AccountResponseDTO.toDTO(accountService.accountById(id)));
    }

    @GetMapping(value = "/withdraw")
    public ResponseEntity<WithdrawalsFreeResponse> withdrawFree (@RequestParam Long id) {
        return ResponseEntity.ok().body(WithdrawalsFreeResponse.consultWithdrawals(accountService.consultWithdrawFree(id)));
    }

    @GetMapping(value = "/balance/")
    public ResponseEntity<BalanceResponse> consultBalance(@RequestParam Long id) {
        return ResponseEntity.ok().body(BalanceResponse.balanceResponse(accountService.accountById(id)));
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> insertAccount(@RequestBody @Valid AccountDTO accountDTO, @RequestParam Long id) {
        Account account = accountService.insertAccount(accountDTO, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(account.getId()).toUri();
        return ResponseEntity.created(uri).body(AccountResponseDTO.toDTO(account));
    }

    @PutMapping
    public ResponseEntity<AccountResponseDTO> updateAccount(@RequestParam Long id, @RequestBody @Valid AccountDTO accountDTO) {
        return ResponseEntity.ok().body(AccountResponseDTO.toDTO(accountService.updateAccount(id, accountDTO)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@RequestParam Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
