package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.BankingOperations;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import com.desafio.manageraccount.dto.response.MessageResponse;
import com.desafio.manageraccount.exceptions.BankingOperationsNotFound;
import com.desafio.manageraccount.repositories.AccountRepository;
import com.desafio.manageraccount.repositories.BankingOperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BankingOperationsServices {

    @Autowired
    private final BankingOperationsRepository bankingOperationsRepository;

    @Autowired
    private AccountRepository accountRepository;

    public BankingOperationsServices(BankingOperationsRepository bankingOperationsRepository) {
        this.bankingOperationsRepository = bankingOperationsRepository;
    }

    public List<BankingOperations> operationsList() {
        List<BankingOperations> operations = bankingOperationsRepository.findAll();
        return operations;
    }

    public MessageResponse insertOperation(BankingOperations operation, Long id) {

        BankingOperations newOperation = bankingOperationsRepository.save(operation);
        newOperation.setAccount(thisAccountIsExist(id));

        MessageResponse messageResponse = null;

        if (newOperation.getTypeOperations() == TypeOperations.DEPOSIT) {
            if (deposit(id, newOperation.getAmount())) {
                newOperation.setTypeStatus(TypeStatus.CONCLUDED);
                messageResponse = createMessageResponse(String.format("Operação de deposito com o ID %d foi efetuada com sucesso", newOperation.getIdOperation()));
            }
        }
//        else if (newOperation.getTypeOperations() == TypeOperations.WITHDRAW) {
//            if (withdraw(id, newOperation.getAmount())) {
//                newOperation.setTypeStatus(TypeStatus.CONCLUDED);
//                messageResponse = createMessageResponse(String.format("Operação de saque com o ID %d foi efetuada com sucesso", newOperation.getIdOperation()));
//            }
//            newOperation.setTypeStatus(TypeStatus.CANCELED);
//        }
        else if (newOperation.getTypeOperations() == TypeOperations.BANKTRANSFER){
            if (banktransfer(id, newOperation.getIdDestinyAccount(), newOperation.getAmount())) {
                newOperation.setTypeStatus(TypeStatus.CONCLUDED);
                messageResponse = createMessageResponse(String.format("Operação de transferência com o ID %d foi efetuada com sucesso", newOperation.getIdOperation()));
            }
            newOperation.setTypeStatus(TypeStatus.CANCELED);
        }
        else {
            messageResponse = createMessageResponse(String.format("Tipo de operação inválida: %s", newOperation.getTypeOperations()));
        }

        bankingOperationsRepository.save(newOperation);


        return messageResponse;
    }

    private Account thisAccountIsExist(Long id) {

        String url = "http://localhost:8080/accounts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        Account account = restTemplate.getForObject(url, Account.class);

        return account;
    }

    private boolean banktransfer(Long idAccountOrigin, Long idAccountDestiny,Double amount) {
        Account accountOrigin = thisAccountIsExist(idAccountOrigin);
        if (amount > accountOrigin.getBalanceAccount()) {
            return false;
        }

        Account accountDestiny = thisAccountIsExist(idAccountDestiny);

        accountOrigin.setBalanceAccount(accountOrigin.getBalanceAccount() - amount);
        accountDestiny.setBalanceAccount(accountDestiny.getBalanceAccount() + amount);

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestiny);

        return true;
    }

    private boolean deposit(Long id, Double amount) {
        Account updateAccount = thisAccountIsExist(id);
        updateAccount.setBalanceAccount(updateAccount.getBalanceAccount() + amount);

        accountRepository.save(updateAccount);

        return true;
    }

//    private boolean withdraw(Long id, Double amount) {
//        boolean sucess = false;
//        Account account = thisAccountIsExist(id);
//
//        if (account.getTypeAccount() == GOVERNMENTPERSON) {
//            if (amount > account.getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("Não tem saldo suficiente para fazer o saque.");
//            }
//            else if (account.getLimitWithdrawals() == 0 && amount + 20.0 > account.getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa de R$ 20,00.");
//            }
//            else if (account.getLimitWithdrawals() == 0) {
//                updateBalance(account.getId(), amount, 0.0);
//                decrementLimitWithdrawals(account.getId());
//                sucess = true;
//            } else  {
//                updateBalance(account.getId(), amount, 0.0);
//                decrementLimitWithdrawals(account.getId());
//                sucess = true;
//            }
//        }
//        else {
//
//            if (amount > account.getBalanceAccount()) {
//                throw new InvalidWithdrawExceptions("Não tem saldo suficiente para fazer o saque.");
//            }
//            else if (account.getLimitWithdrawals() == 0 && amount + 10.0 > account.getBalanceAccount()) {
//                System.out.println(new InvalidWithdrawExceptions("O limite de saques gratuitos deste mês acabou e não tem saldo suficiente para fazer devido a taxa de R$ 10,00."));
//            }
//            else if (account.getLimitWithdrawals() == 0) {
//                updateBalance(account.getId(), amount, 10.0);
//                decrementLimitWithdrawals(account.getId());
//                sucess = true;
//            } else {
//                updateBalance(account.getId(), amount, 0.0);
//                decrementLimitWithdrawals(account.getId());
//                sucess = true;
//            }
//        }
//        return sucess;
//    }

    private void updateBalance(Long id,Double amount, Double rate) {
        Account account = thisAccountIsExist(id);
        account.setBalanceAccount((account.getBalanceAccount() - (amount + rate)));
        accountRepository.save(account);
    }

//    private void decrementLimitWithdrawals(Long id) {
//        Account account = thisAccountIsExist(id);
//        account.setLimitWithdrawals(account.getLimitWithdrawals() - 1);
//        accountRepository.save(account);
//    }

    public BankingOperations operationById(Long id) {
        idIsExist(id);
        BankingOperations operation = bankingOperationsRepository.findById(id).get();
        return operation;
    }

    private BankingOperations idIsExist(Long id) {
        return bankingOperationsRepository.findById(id).orElseThrow(() -> new BankingOperationsNotFound(id));
    }


    private MessageResponse createMessageResponse (String textMessage) {
        return MessageResponse.builder().message(textMessage).build();
    }
}
