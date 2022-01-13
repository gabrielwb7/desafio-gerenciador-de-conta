package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.response.MessageResponse;
import com.desafio.manageraccount.exceptions.AccountAlreadyRegisteredException;
import com.desafio.manageraccount.exceptions.AccountNotFoundException;
import com.desafio.manageraccount.exceptions.InvalidWithdrawExceptions;
import com.desafio.manageraccount.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.desafio.manageraccount.entities.TypeAccount.GOVERNMENTPERSON;
import static com.desafio.manageraccount.entities.TypeAccount.REGULARPERSON;

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

    public MessageResponse insertAccount(Account account, Long id) {

        thisAccountAlreadyExists(account);

        if (!theLimitWithdrawalsIsCorrect(account)) {
            throw new InvalidWithdrawExceptions("Limite de saques gratuitos não está de acordo com o tipo da conta");
        }

        String url = "http://localhost:8080/clients/" + id;
        RestTemplate restTemplate = new RestTemplate();
        Client client = restTemplate.getForObject(url, Client.class);

        account.setClient(client);

        Account newAccount = accountRepository.save(account);
        return createMessageResponse(String.format("Conta com o ID %d foi criada com sucesso!!", newAccount.getId()));
    }

    public void delete(Long id) {
        idIsExist(id);
        accountRepository.deleteById(id);
    }

    public MessageResponse updateAccount(Long id, Account account) {
        idIsExist(id);
        thisAccountAlreadyExists(account);
        if (!theLimitWithdrawalsIsCorrect(account)) {
            throw new InvalidWithdrawExceptions("Limite de saques gratuitos não está de acordo com o tipo de conta");
        }

        Account updateAccount = accountRepository.getById(id);
        updateAccount.setNumberAccount(account.getNumberAccount());
        updateAccount.setTypeAccount(account.getTypeAccount());
        updateAccount.setAgency(account.getAgency());
        updateAccount.setVerifyingDigit(account.getVerifyingDigit());

        accountRepository.save(updateAccount);
        return createMessageResponse(String.format("Conta com o ID %d foi atualizada", updateAccount.getId()));
    }

    public Account accountById(Long id) {
        idIsExist(id);
        Account account = accountRepository.findById(id).get();
        return account;
    }

    private Account idIsExist(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    private boolean theLimitWithdrawalsIsCorrect(Account account) {
        if(account.getTypeAccount() == GOVERNMENTPERSON && account.getLimitWithdrawals() > 250) {
            return false;
        }
        if(account.getTypeAccount() == REGULARPERSON && account.getLimitWithdrawals() > 5) {
            return false;
        }
        if(account.getLimitWithdrawals() > 50) {
            return false;
        }
        return true;
    }

    private MessageResponse createMessageResponse (String textMessage) {
        return MessageResponse.builder().message(textMessage).build();
    }

    private void thisAccountAlreadyExists(Account newAccount) {
        List<Account> allAccounts = accountRepository.findAll();
        for (Account account : allAccounts) {
            if (account.equals(newAccount)) {
                throw new AccountAlreadyRegisteredException("Dados incorretos! Os dados informados já estão cadastrados");
            }
        }
    }


//    public List<Account> accountsPerClient(Long id) {
//        Client client = clientRepository.findById(id).get();
//        List<Account> allAccounts = new ArrayList<>(client.getAccountList());
//        return allAccounts;
//    }

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
