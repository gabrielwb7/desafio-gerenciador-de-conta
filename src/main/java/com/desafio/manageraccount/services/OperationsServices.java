package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.OperationsRepository;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.BankingOperationsNotFound;
import com.desafio.manageraccount.services.exceptions.InvalidOperationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OperationsServices {

    @Autowired
    private final OperationsRepository operationsRepository;

    @Autowired
    private AccountRepository accountRepository;

    public OperationsServices(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }


    public List<Operations> operationsList() {
        List<Operations> operations = operationsRepository.findAll();
        return operations;
    }

    public Operations insertOperation(Operations operation, Long id) {
        if (operation.getAmount() <= 0) {
            throw new InvalidOperationExceptions("Valor da operação inválida");
        }

        Operations newOperation = operationsRepository.save(operation);
        newOperation.setAccount(accountRepository.getById(id));

        if (newOperation.getTypeOperations() == TypeOperations.DEPOSIT) {
            deposit(id, newOperation.getAmount());
            newOperation.setTypeStatus(TypeStatus.CONCLUDED);
            return operationsRepository.save(newOperation);
        }
        if (newOperation.getTypeOperations() == TypeOperations.BANKTRANSFER){
            banktransfer(id, newOperation);
            newOperation.setTypeStatus(TypeStatus.CONCLUDED);
            return operationsRepository.save(newOperation);
        }
        if (newOperation.getTypeOperations() == TypeOperations.WITHDRAW) {
                withdraw(id, newOperation.getAmount());
                newOperation.setTypeStatus(TypeStatus.CONCLUDED);
                return operationsRepository.save(newOperation);
        }
        newOperation.setTypeStatus(TypeStatus.CANCELED);
        return operationsRepository.save(newOperation);
    }

    public Operations operationById(Long id) {
        idIsExist(id);
        Operations operation = operationsRepository.findById(id).get();
        return operation;
    }

    private void deposit(Long id, Double amount) {
        Account updateAccount = accountRepository.getById(id);
        updateAccount.setBalanceAccount(updateAccount.getBalanceAccount() + amount);

        accountRepository.save(updateAccount);
    }

    private void banktransfer(Long idAccountOrigin, Operations operation) {
        Account accountOrigin = accountRepository.getById(idAccountOrigin);

        if (operation.getAmount() > accountOrigin.getBalanceAccount()) {
            throw new InvalidOperationExceptions("Saldo insuficiente!");
        }
        if (accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit()) == null) {
            throw new AccountNotFoundException("A conta destino não existe");
        }

        Long id = accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit()).getId();

        Account accountDestiny = accountRepository.getById(id);

        accountOrigin.setBalanceAccount(accountOrigin.getBalanceAccount() - operation.getAmount());
        accountDestiny.setBalanceAccount(accountDestiny.getBalanceAccount() + operation.getAmount());

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestiny);

    }

    private void withdraw(Long id, Double amount) {

        Account account = accountRepository.getById(id);

        if (amount > account.getBalanceAccount()) {
            throw new InvalidOperationExceptions("Não tem saldo suficiente para fazer o saque.");
        }
        if (verifyWithdrawals(account.getId()) < account.getTypeAccount().getMaxLimitWithdrawals()) {
            account.setBalanceAccount((account.getBalanceAccount() - amount));
        }
        else {
            if (account.getTypeAccount().calculateWithdraw(amount) > account.getBalanceAccount()) {
                throw new InvalidOperationExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa:  " + account.getTypeAccount().getTax());
            }

            account.setBalanceAccount(account.getBalanceAccount() - account.getTypeAccount().calculateWithdraw(amount));
        }
        accountRepository.save(account);
        updateWithdrawals(account.getId());
    }

    private void updateWithdrawals(Long id) {
        String url = "http://localhost:8090/withdrawals/" + id;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(url, id);
    }

    private Integer verifyWithdrawals(Long id) {
        String url = "http://localhost:8090/withdrawals/" + id;
        RestTemplate restTemplate = new RestTemplate();
        Integer withdraw = restTemplate.getForObject(url, Integer.class);
        return withdraw;
    }

    private Operations idIsExist(Long id) {
        return operationsRepository.findById(id).orElseThrow(() -> new BankingOperationsNotFound(id));
    }

}
