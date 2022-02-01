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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class OperationsServices {

    @Autowired
    private final OperationsRepository operationsRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final Tax tax = new Tax();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public OperationsServices(OperationsRepository operationsRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.operationsRepository = operationsRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Operations> operationsList() {
        List<Operations> operations = operationsRepository.findAll();
        return operations;
    }

    public List<Operations> statement(Long id) {
        List<Operations> operationsList = operationsRepository.findByAccountId(id);
        return operationsList;
    }

    public Operations insertOperation(Operations operation, Long id) {
        if (operation.getAmount() <= 0) {
            throw new InvalidOperationExceptions("Valor da operação inválida");
        }

        Operations newOperation = operationsRepository.save(operation);
        newOperation.setAccount(accountRepository.getById(id));

        if (newOperation.getTypeOperations() == TypeOperations.DEPOSIT) {
            return operationsRepository.save(deposit(id, newOperation));
        }
        if (newOperation.getTypeOperations() == TypeOperations.BANKTRANSFER){
            return operationsRepository.save(banktransfer(id, newOperation));
        }
        if (newOperation.getTypeOperations() == TypeOperations.WITHDRAW) {
            return operationsRepository.save(withdraw(id, newOperation));
        }
        return operationsRepository.save(newOperation);
    }

    public Operations operationById(Long id) {
        idIsExist(id);
        return operationsRepository.findById(id).get();
    }

    private Operations deposit(Long id, Operations operations) {
        Account updateAccount = accountRepository.getById(id);
        updateAccount.setBalanceAccount(updateAccount.getBalanceAccount() + operations.getAmount());

        accountRepository.save(updateAccount);

        operations.setTypeStatus(TypeStatus.CONCLUDED);

        return operations;
    }

    private Operations banktransfer(Long idAccountOrigin, Operations operation) {
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

        operation.setTypeStatus(TypeStatus.CONCLUDED);

        return operation;
    }

    private Operations withdraw(Long id, Operations operations) {

        Account account = accountRepository.getById(id);

        if (operations.getAmount() > account.getBalanceAccount()) {
            throw new InvalidOperationExceptions("Não tem saldo suficiente para fazer o saque.");
        }
        if (verifyWithdrawals(account.getId()) != 0) {
            account.setBalanceAccount((account.getBalanceAccount() - operations.getAmount()));
            operations.setTypeStatus(TypeStatus.CONCLUDED);
            newWithdraw(operations.getIdOperation());
        }
        else {
            if (tax.calculateWithdrawWithTax(account, operations.getAmount()) > account.getBalanceAccount()) {
                throw new InvalidOperationExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa:  " +  tax.returnTax(account));
            }
            account.setBalanceAccount(account.getBalanceAccount() - tax.calculateWithdrawWithTax(account, operations.getAmount()));
            operations.setTypeStatus(TypeStatus.CONCLUDED);
            operations.setTax(tax.returnTax(account));
        }

        accountRepository.save(account);
        return operations;
    }

    private void newWithdraw(Long id) {
        Operations operations = operationsRepository.getById(id);
        String data = String.format("{\"idAccount\":%d,\"amount\":\"%.2f\",\"date\":\"%s\"}", operations.getAccount().getId(), operations.getAmount(), sdf.format(operations.getDateOperation()));
        kafkaTemplate.send("newWithdraw", data);
    }

    private Integer verifyWithdrawals(Long id) {
        Jedis jedis = new Jedis();
        return Integer.parseInt(jedis.get(Long.toString(id)));
    }

    private Operations idIsExist(Long id) {
        return operationsRepository.findById(id).orElseThrow(() -> new BankingOperationsNotFound(id));
    }

}
