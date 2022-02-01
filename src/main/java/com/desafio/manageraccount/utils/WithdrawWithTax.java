package com.desafio.manageraccount.utils;

import com.desafio.manageraccount.entities.Account;

public interface WithdrawWithTax {

    Double returnTax(Account account);
    Double calculateWithdrawWithTax (Account account, double amount);

}
