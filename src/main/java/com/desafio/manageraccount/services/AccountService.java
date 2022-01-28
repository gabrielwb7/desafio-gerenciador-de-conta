package com.desafio.manageraccount.services;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.ClientRepository;
import com.desafio.manageraccount.services.exceptions.AccountAlreadyRegisteredException;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Account> listAllAccounts() {
        return accountRepository.findAll();
    }

    public Account insertAccount(AccountDTO accountDTO, Long id) {

        accountDTO.validatesDataAccount(accountDTO.getAgency(), accountDTO.getNumberAccount(), accountDTO.getVerifyDigit());

        if (accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(accountDTO.getAgency(), accountDTO.getNumberAccount(), accountDTO.getVerifyDigit()) != null) {
            throw new AccountAlreadyRegisteredException("Dados incorretos! Os dados informados já estão cadastrados");
        }

        Client client = clientRepository.getById(id);

        if (accountDTO.getTypeAccount() == TypeAccount.REGULARPERSON && client.getClientCPF() == null) {
            throw new DocumentationException("O cliente não tem CPF cadastrado para abrir conta normal");
        }
        if (accountDTO.getTypeAccount() == TypeAccount.LEGALPERSON && client.getClientCNPJ() == null) {
            throw new DocumentationException("O cliente não tem CNPJ cadastrado para abrir conta juridica");
        }

        Account account = accountRepository.save(accountDTO.toDTO());
        account.setClient(client);

        setWithdraws(account);

        return accountRepository.save(account);
    }

    public void delete(Long id) {
        idIsExist(id);
        accountRepository.deleteById(id);
    }

    public Account updateAccount(Long id, AccountDTO accountDTO) {
        idIsExist(id);
        accountDTO.validatesDataAccount(accountDTO.getAgency(), accountDTO.getNumberAccount(), accountDTO.getVerifyDigit());

        if (accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(accountDTO.getAgency(), accountDTO.getNumberAccount(), accountDTO.getVerifyDigit()) != null) {
            throw new AccountAlreadyRegisteredException("Dados incorretos! Os dados informados já estão cadastrados");
        }

        Account updateAccount = accountRepository.getById(id);

        updateAccount.setNumberAccount(accountDTO.getNumberAccount());
        updateAccount.setAgency(accountDTO.getAgency());
        updateAccount.setVerifyDigit(accountDTO.getVerifyDigit());

        return accountRepository.save(updateAccount);
    }

    public Account accountById(Long id) {
        idIsExist(id);
        return accountRepository.findById(id).get();
    }

    public Account consultBalance(Long id) {
        idIsExist(id);
        Account account = accountRepository.findById(id).get();
        return account;
    }

    private Account idIsExist(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("A conta com o id " + id +" não existe"));
    }

    private void setWithdraws(Account account) {
        String data = String.format("{\"id\":%d,\"limit\":\"%d\"}", account.getId(), account.getTypeAccount().getMaxLimitWithdrawals());
        kafkaTemplate.send("newAccount", data);
    }

//    public List<Account> accountsPerClient(Long id) {
//        Client client = clientRepository.findById(id).get();
//        List<Account> allAccounts = new ArrayList<>(client.getAccountList());
//        return allAccounts;
//    }


}
