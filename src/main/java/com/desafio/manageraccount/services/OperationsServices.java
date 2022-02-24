package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.Tax;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.OperationsRepository;
import com.desafio.manageraccount.services.exceptions.*;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

@Service
public class OperationsServices {

    private final OperationsRepository operationsRepository;
    private final AccountRepository accountRepository;
    private final Tax tax = new Tax();
    private final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost",6379);

    private final ProduceMessage producer = new ProduceMessage();

    public OperationsServices(OperationsRepository operationsRepository, AccountRepository accountRepository) {
        this.operationsRepository = operationsRepository;
        this.accountRepository = accountRepository;
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
        operation.setTypeOperations(TypeOperations.DEPOSIT);
        Account updateAccount = accountIsExist(id);

        validateAmountValue(updateAccount,operation);

        updateAccount.setBalanceAccount(updateAccount.getBalanceAccount() + operation.getAmount());

        accountRepository.save(updateAccount);

        operation.setAccount(updateAccount);
        operation.setTypeStatus(TypeStatus.CONCLUDED);

        return operationsRepository.save(operation);
    }

    public Operations bankTransfer(Long idAccountOrigin, Operations operation) {
        Account accountOrigin = accountIsExist(idAccountOrigin);
        operation.setTypeOperations(TypeOperations.BANKTRANSFER);

        validateAmountValue(accountOrigin,operation);

        validatesDataAccount(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit());

        if (accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit()) == null) {
            throw new AccountNotFoundException("A conta destino não existe");
        }

        Long id = accountRepository.findByAgencyAndNumberAccountAndVerifyDigit(operation.getAgencyDestiny(), operation.getAccountDestiny(), operation.getDestinyVerifyDigit()).getId();
        Account accountDestiny = accountIsExist(id);

        accountOrigin.setBalanceAccount(accountOrigin.getBalanceAccount() - operation.getAmount());
        accountDestiny.setBalanceAccount(accountDestiny.getBalanceAccount() + operation.getAmount());

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestiny);

        operation.setAccount(accountOrigin);
        operation.setTypeStatus(TypeStatus.CONCLUDED);

        return operationsRepository.save(operation);
    }

    public Operations withdraw(Long id, Operations operation) {
        operation.setTypeOperations(TypeOperations.WITHDRAW);
        double valueOfOperation;
        Jedis jedis = pool.getResource();

        Account account = accountIsExist(id);

        validateAmountValue(account, operation);

        if (Integer.parseInt(jedis.get(Long.toString(id))) != 0) {
            valueOfOperation = account.getBalanceAccount() - operation.getAmount();
            operation.setTypeStatus(TypeStatus.CONCLUDED);
        }
        else {
            if (tax.calculateWithdrawWithTax(account, operation.getAmount()) > account.getBalanceAccount()) {
                throw new InvalidOperationExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa:  " +  tax.returnTax(account));
            }
            valueOfOperation = account.getBalanceAccount() - tax.calculateWithdrawWithTax(account, operation.getAmount());
            operation.setTypeStatus(TypeStatus.CONCLUDED);
            operation.setTax(tax.returnTax(account));
        }
        operation.setAccount(account);

        String data = String.format("{\"idAccount\":%d,\"amount\":\"%.2f\",\"date\":\"%s\",\"tax\":\"%.2f\"}", operation.getAccount().getId(), operation.getAmount(), operation.getDateOperation(), operation.getTax());
        try {
           producer.sendMessage("NEW_WITHDRAW", data);

            account.setBalanceAccount(valueOfOperation);
            accountRepository.save(account);

            return operationsRepository.save(operation);
        } catch (Exception e) {
            throw new CouldNotCompleteTheRequest("Serviço indisponivel");
        }
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

    private void validateAmountValue(Account account, Operations operation) {
        if (operation.getAmount() <= 0) {
            throw new InvalidOperationExceptions("Valor da operação inválida");
        }
        if (operation.getTypeOperations() != TypeOperations.DEPOSIT) {
            if (operation.getAmount() > account.getBalanceAccount()) {
                throw new InvalidOperationExceptions("Não tem saldo suficiente para fazer a operação.");
            }
        }
    }

    private void validatesDataAccount(String agency, String numberAccount, String verifyDigit) {
        boolean validate = agency.matches("^\\d+$") && numberAccount.matches("^\\d+$") && verifyDigit.matches("^\\d+$");
        if (!validate) {
            throw new DocumentationException("Os dados informados da conta destino estão inválidos: "
                    + "agency - " + agency
                    + ", account - " + numberAccount
                    + ", verify digit - " + verifyDigit);
        }
    }
}
