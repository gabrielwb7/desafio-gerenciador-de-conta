package com.desafio.manageraccount.entities;

import com.desafio.manageraccount.utils.WithdrawWithTax;

public class Tax implements WithdrawWithTax {


    @Override
    public Double returnTax(Account account) {
        return account.getTypeAccount().getTax();
    }

    @Override
    public Double calculateWithdrawWithTax(Account account, double amount) {
        return amount + account.getTypeAccount().getTax();
    }
}
