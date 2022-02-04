package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.Tax;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.OperationsRepository;
import com.desafio.manageraccount.services.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.services.exceptions.BankingOperationsNotFound;
import com.desafio.manageraccount.services.exceptions.InvalidOperationExceptions;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class OperationsServices {

    private final OperationsRepository operationsRepository;

    private final AccountRepository accountRepository;

    private final Tax tax = new Tax();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OperationsServices(OperationsRepository operationsRepository, AccountRepository accountRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.operationsRepository = operationsRepository;
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Operations> operationsList() {
        return operationsRepository.findAll();
    }

    public List<Operations> statement(Long id) {
        accountIsExist(id);
        return allOperationsForAccount(id);
    }

    public Operations operationById(Long id) {
        return idIsExist(id);
    }

    public Operations deposit(Long id, Operations operation) {
        if (operation.getAmount() <= 0) {
            throw new InvalidOperationExceptions("Valor da operação inválida");
        }
        Account updateAccount = accountIsExist(id);
        updateAccount.setBalanceAccount(updateAccount.getBalanceAccount() + operation.getAmount());

        accountRepository.save(updateAccount);

        operation.setTypeStatus(TypeStatus.CONCLUDED);
        operation.setTypeOperations(TypeOperations.DEPOSIT);

        return operationsRepository.save(operation);
    }

    public Operations bankTransfer(Long idAccountOrigin, Operations operation) {
        if (operation.getAmount() <= 0) {
            throw new InvalidOperationExceptions("Valor da operação inválida");
        }

        Account accountOrigin = accountIsExist(idAccountOrigin);

        if (operation.getAmount() > accountOrigin.getBalanceAccount()) {
            throw new InvalidOperationExceptions("Saldo insuficiente!");
        }
        if (accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit()) == null) {
            throw new AccountNotFoundException("A conta destino não existe");
        }

        Long id = accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit()).getId();
        Account accountDestiny = accountIsExist(id);

        accountOrigin.setBalanceAccount(accountOrigin.getBalanceAccount() - operation.getAmount());
        accountDestiny.setBalanceAccount(accountDestiny.getBalanceAccount() + operation.getAmount());

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestiny);

        operation.setTypeStatus(TypeStatus.CONCLUDED);
        operation.setTypeOperations(TypeOperations.BANKTRANSFER);

        return operationsRepository.save(operation);
    }

    public Operations withdraw(Long id, Operations operation) {
        if (operation.getAmount() <= 0) {
            throw new InvalidOperationExceptions("Valor da operação inválida");
        }

        Account account = accountIsExist(id);

        if (operation.getAmount() > account.getBalanceAccount()) {
            throw new InvalidOperationExceptions("Não tem saldo suficiente para fazer o saque.");
        }
        if (verifyWithdrawals(account.getId()) != 0) {
            account.setBalanceAccount((account.getBalanceAccount() - operation.getAmount()));
            operation.setTypeStatus(TypeStatus.CONCLUDED);
        }
        else {
            if (tax.calculateWithdrawWithTax(account, operation.getAmount()) > account.getBalanceAccount()) {
                throw new InvalidOperationExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa:  " +  tax.returnTax(account));
            }
            account.setBalanceAccount(account.getBalanceAccount() - tax.calculateWithdrawWithTax(account, operation.getAmount()));
            operation.setTypeStatus(TypeStatus.CONCLUDED);
            operation.setTax(tax.returnTax(account));
        }
        accountRepository.save(account);
        operation.setTypeOperations(TypeOperations.WITHDRAW);
        operation.setAccount(account);
        newWithdraw(operation);

        return operationsRepository.save(operation);
    }

    private void newWithdraw(Operations operation) {
        String data = String.format("{\"idAccount\":%d,\"amount\":\"%.2f\",\"date\":\"%s\",\"tax\":\"%.2f\"}", operation.getAccount().getId(), operation.getAmount(), operation.getDateOperation(), operation.getTax());
        kafkaTemplate.send("newWithdraw", data);
    }

    private Integer verifyWithdrawals(Long id) {
        Jedis jedis = new Jedis();
        return Integer.parseInt(jedis.get(Long.toString(id)));
    }

    private Operations idIsExist(Long id) {
        return operationsRepository.findById(id).orElseThrow(() -> new BankingOperationsNotFound(id));
    }

    private Account accountIsExist(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("A conta com o id " + id +" não existe"));
    }

    private List<Operations> allOperationsForAccount(Long id) {
        List<Operations> operationsList = operationsRepository.findByAccountId(id);
        Account account = accountRepository.getById(id);

        List<Operations> transferList = operationsRepository.findByAccountDestinyAndAgencyDestinyAndDestinyVerifyDigit(account.getNumberAccount(), account.getAgency(), account.getVerifyDigit());

        operationsList.addAll(transferList);

        return operationsList;
    }

}
