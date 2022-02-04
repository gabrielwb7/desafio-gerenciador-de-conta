package com.desafio.manageraccount.dto.response.responsesaccount;

import com.desafio.manageraccount.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WithdrawalsFreeResponse {

    private Integer withdrawsFree;

    public static WithdrawalsFreeResponse consultWithdrawals (Account account) {
        return new WithdrawalsFreeResponse(account.getQuantityWithdraw());
    }
}
