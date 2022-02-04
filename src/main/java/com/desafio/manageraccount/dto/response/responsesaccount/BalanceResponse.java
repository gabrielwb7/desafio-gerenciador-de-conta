package com.desafio.manageraccount.dto.response.responsesaccount;

import com.desafio.manageraccount.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BalanceResponse {

    private Double balanceAccount;

    public static BalanceResponse balanceResponse(Account account) {
        return new BalanceResponse(account.getBalanceAccount());
    }
}
