package com.desafio.manageraccount.services;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.services.exceptions.AccountAlreadyRegisteredException;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> listAllAccounts() {
        List<Account> allAccounts = accountRepository.findAll();
        return allAccounts;
    }

    public Account insertAccount(AccountDTO accountDTO, Long id) {

        thisAccountAlreadyExists(accountDTO);

        String url = "http://localhost:8080/clients/" + id;
        RestTemplate restTemplate = new RestTemplate();
        Client client = restTemplate.getForObject(url, Client.class);

        if (accountDTO.getTypeAccount() == TypeAccount.LEGALPERSON && client.getClientCNPJ() == null) {
            throw new DocumentationException("O cliente não tem CNPJ cadastrado para abrir conta juridica");
        }
        Account account = accountRepository.save(accountDTO.toDTO());
        account.setClient(client);

        return accountRepository.save(account);
    }

    public void delete(Long id) {
        idIsExist(id);
        accountRepository.deleteById(id);
    }

    public Account updateAccount(Long id, AccountDTO accountDTO) {
        idIsExist(id);
        thisAccountAlreadyExists(accountDTO);

        Account updateAccount = accountRepository.getById(id);
        updateAccount.setNumberAccount(accountDTO.getNumberAccount());
        updateAccount.setAgency(accountDTO.getAgency());
        updateAccount.setVerifyDigit(accountDTO.getVerifyDigit());

        return accountRepository.save(updateAccount);
    }

    public Account accountById(Long id) {
        idIsExist(id);
        Account account = accountRepository.findById(id).get();
        return account;
    }

    public String consultBalance(Long id) {
        idIsExist(id);
        Account account = accountRepository.findById(id).get();
        return String.format("Saldo: R$ %.2f", account.getBalanceAccount());
    }

    private Account idIsExist(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    private void thisAccountAlreadyExists(AccountDTO accountDTO) {
        List<Account> allAccounts = accountRepository.findAll();
        for (Account account : allAccounts) {
            if (account.equals(accountDTO.toDTO())) {
                throw new AccountAlreadyRegisteredException("Dados incorretos! Os dados informados já estão cadastrados");
            }
        }
    }

//    public List<Account> accountsPerClient(Long id) {
//        Client client = clientRepository.findById(id).get();
//        List<Account> allAccounts = new ArrayList<>(client.getAccountList());
//        return allAccounts;
//    }


}
