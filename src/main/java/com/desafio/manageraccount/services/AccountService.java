package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

    public Account returnOneAccount(Long id) {
        Account account = idIsExist(id);
        return account;
    }

    public Account insertAccount(Account account, Long id) {

        String url = "http://localhost:8080/clients/" + id;
        RestTemplate restTemplate = new RestTemplate();
        Client client = restTemplate.getForObject(url, Client.class);

        account.setClient(client);

        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    public void delete(Long id) {
        idIsExist(id);
        accountRepository.deleteById(id);
    }

    public Account updateAccount(Long id, Account account) {
        idIsExist(id);
        Account updateAccount = accountRepository.getById(id);
        updateAccount.setNumberAccount(account.getNumberAccount());
        updateAccount.setTypeAccount(account.getTypeAccount());
        updateAccount.setAgency(account.getAgency());
        updateAccount.setVerifyingDigit(account.getVerifyingDigit());

        accountRepository.save(updateAccount);

        return updateAccount;
    }

    public Account accountById(Long id) {
        Account account = accountRepository.findById(id).get();
        return account;
    }

//    public List<Account> accountsPerClient(Long id) {
//        Client client = clientRepository.findById(id).get();
//        List<Account> allAccounts = new ArrayList<>(client.getAccountList());
//        return allAccounts;
//    }

    private Account idIsExist(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

//    public void deposit(Double amount) {
//        balanceAccount += amount;
//    };
//
//    public void withdraw(Double amount) {
//        if (getTypeAccount() == GOVERNMENTPERSON) {
//            if (amount > getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("Não tem saldo suficiente para fazer o saque.");
//            }
//            if (getLimitWithdrawals() == 0 && amount + 20.0 > getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa de R$ 20,00.");
//            }
//            if (getLimitWithdrawals() == 0) {
//                updateBalance(amount,20.0);
//                decrementLimitWithdrawals();
//            } else  {
//                updateBalance(amount, 0.0);
//                decrementLimitWithdrawals();
//            }
//        }
//        else {
//
//            if (amount > getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("Não tem saldo suficiente para fazer o saque.");
//            }
//            if (getLimitWithdrawals() == 0 && amount + 10.0 > getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa de R$ 10,00.");
//            }
//            if (getLimitWithdrawals() == 0) {
//                updateBalance(amount, 10.0);
//                decrementLimitWithdrawals();
//            } else {
//                updateBalance(amount, 0.0);
//                decrementLimitWithdrawals();
//            }
//        }
//    }
//
//    public void updateBalance(Double amount, Double rate) {
//        balanceAccount -= amount + rate;
//    }
//
//    public void decrementLimitWithdrawals () {
//        limitWithdrawals -= 1;
//    }
}
