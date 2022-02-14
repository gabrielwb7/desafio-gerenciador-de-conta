package com.desafio.manageraccount.services;

import com.desafio.manageraccount.config.ProduceMessage;
import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.ClientRepository;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.exceptions.CouldNotCompleteTheRequest;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Random;

@Service
public class AccountService extends ProduceMessage{

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final Random random = new Random();

    public AccountService(AccountRepository accountRepository, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    public List<Account> listAllAccounts() {
        return accountRepository.findAll();
    }

    public Account insertAccount(AccountDTO accountDTO, Long id) {

        Client client = clientIsExist(id);

        if (accountDTO.getTypeAccount() == TypeAccount.REGULARPERSON && client.getClientCPF() == null) {
            throw new DocumentationException("O cliente não tem CPF cadastrado para abrir conta normal");
        }
        if (accountDTO.getTypeAccount() == TypeAccount.LEGALPERSON && client.getClientCNPJ() == null) {
            throw new DocumentationException("O cliente não tem CNPJ cadastrado para abrir conta juridica");
        }

        Account createAccount = validatesDataAccount(accountDTO);
        createAccount.setClient(client);
        createAccount.setQuantityWithdraw(createAccount.getTypeAccount().getMaxLimitWithdrawals());
        String data = String.format("{\"id\":%d,\"limitWithdraw\":\"%d\"}", accountRepository.nextID(), createAccount.getTypeAccount().getMaxLimitWithdrawals());
        try {
            sendMessage("NEW_ACCOUNT", data);
        } catch (Exception e) {
            throw new CouldNotCompleteTheRequest("Serviço indisponivel");
        }
        return accountRepository.save(createAccount);
    }

    public void delete(Long id) {
        idIsExist(id);
        accountRepository.deleteById(id);
    }

    public Account updateAccount(Long id, AccountDTO accountDTO) {
        Account beforeAccount = idIsExist(id);
        Account updateAccount = validatesDataAccount(accountDTO);

        beforeAccount.setNumberAccount(updateAccount.getNumberAccount());
        beforeAccount.setAgency(updateAccount.getAgency());
        beforeAccount.setVerifyDigit(updateAccount.getVerifyDigit());

        return accountRepository.save(beforeAccount);
    }

    public Account consultWithdrawFree(Long id) {

        Account account = idIsExist(id);
        Jedis jedis = new Jedis();

        account.setQuantityWithdraw(Integer.valueOf(jedis.get(Long.toString(id))));

        accountRepository.save(account);

        return account;
    }

    public Account accountById(Long id) {
        return idIsExist(id);
    }

    private Account idIsExist(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("A conta com o id " + id +" não existe"));
    }

    private Client clientIsExist(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(("O cliente com o id " + id + " não foi encontrado")));
    }

    private String generateNumberForAccount() {
        StringBuilder numberAccount = new StringBuilder(Integer.toString(random.nextInt(10)));

        for (int i = 0; i < 4; i++) {
            numberAccount.append(random.nextInt(10));
        }
        return String.valueOf(numberAccount);
    }

    private Account validatesDataAccount(AccountDTO accountDTO) {
        Account account = accountDTO.toDTO();

        boolean validate = account.getAgency().matches("^\\d+$");
        if (!validate) {
            throw new DocumentationException("O número da agência está incorreto: "
                    + "agency - " + accountDTO.getAgency());
        }

        account.setNumberAccount(generateNumberForAccount());
        account.setVerifyDigit(Integer.toString(random.nextInt(10)));

        while(accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(account.getAgency(), account.getNumberAccount(), account.getVerifyDigit()) != null) {
            account.setNumberAccount(generateNumberForAccount());
            account.setVerifyDigit(Integer.toString(random.nextInt(10)));
        }
        return account;
    }

}
