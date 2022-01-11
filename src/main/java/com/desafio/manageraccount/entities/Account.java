package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.exceptions.InvalidWithdrawExceptions;

import javax.persistence.*;
import java.util.Objects;

import static com.desafio.manageraccount.entities.TypeAccount.GOVERNMENTPERSON;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String agency;
    private String numberAccount;
    private TypeAccount typeAccount;
    private String verifyingDigit;
    private Double balanceAccount;
    private Double valueWithdrawLimit;
    private Integer limitWithdrawals;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Account(Long id, String agency, String numberAccount, TypeAccount typeAccount, String verifyingDigit, Double balanceAccount, Double valueWithdrawLimit, Integer limitWithdrawals, Client client) {
        this.id = id;
        this.agency = agency;
        this.numberAccount = numberAccount;
        this.typeAccount = typeAccount;
        this.verifyingDigit = verifyingDigit;
        this.balanceAccount = balanceAccount;
        this.valueWithdrawLimit = valueWithdrawLimit;
        this.limitWithdrawals = limitWithdrawals;
        this.client = client;
    }

    public void deposit(Double amount) {
        balanceAccount += amount;
    };

    public void withdraw(Double amount) {
        if (getTypeAccount() == GOVERNMENTPERSON) {
            if (amount > getBalanceAccount()) {
                throw new InvalidWithdrawExceptions("Não tem saldo suficiente para fazer o saque.");
            }
            if (getLimitWithdrawals() == 0 && amount + 20.0 > getBalanceAccount()) {
                throw new InvalidWithdrawExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa de R$ 20,00.");
            }
            if (getLimitWithdrawals() == 0) {
                updateBalance(amount,20.0);
                decrementLimitWithdrawals();
            } else  {
                updateBalance(amount, 0.0);
                decrementLimitWithdrawals();
            }
        }
        else {

            if (amount > getBalanceAccount()) {
                throw new InvalidWithdrawExceptions("Não tem saldo suficiente para fazer o saque.");
            }
            if (getLimitWithdrawals() == 0 && amount + 10.0 > getBalanceAccount()) {
                throw new InvalidWithdrawExceptions("O limite de saques gratuitos acabou e não tem saldo suficiente para fazer devido a taxa de R$ 10,00.");
            }
            if (getLimitWithdrawals() == 0) {
                updateBalance(amount, 10.0);
                decrementLimitWithdrawals();
            } else {
                updateBalance(amount, 0.0);
                decrementLimitWithdrawals();
            }
        }
    }

    public void updateBalance(Double amount, Double rate) {
        balanceAccount -= amount + rate;
    }

    public void decrementLimitWithdrawals () {
        limitWithdrawals -= 1;
    }

    public Long getId() {
        return id;
    }

    public String getAgency() {
        return agency;
    }

    public String getNumberAccount() {
        return numberAccount;
    }

    public void setNumberAccount(String numberAccount) {
        this.numberAccount = numberAccount;
    }

    public TypeAccount getTypeAccount() {
        return typeAccount;
    }

    public String getVerifyingDigit() {
        return verifyingDigit;
    }

    public Double getBalanceAccount() {
        return balanceAccount;
    }

    public Double getValueWithdrawLimit() {
        return valueWithdrawLimit;
    }

    public void setValueWithdrawLimit(Double valueWithdrawLimit) {
        this.valueWithdrawLimit = valueWithdrawLimit;
    }

    public Integer getLimitWithdrawals() {
        return limitWithdrawals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(agency, account.agency) && Objects.equals(numberAccount, account.numberAccount) && Objects.equals(verifyingDigit, account.verifyingDigit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, agency, numberAccount, verifyingDigit);
    }
}
