package com.desafio.manageraccount.entities.enums;

import com.desafio.manageraccount.utils.Withdraw;
import lombok.Getter;

@Getter
public enum TypeAccount implements Withdraw {

    LEGALPERSON(50, 10.0),
    REGULARPERSON(5, 10.0),
    GOVERNMENTPERSON(250, 20.0);

    private Integer maxLimitWithdrawals;
    private Double tax;

    TypeAccount(Integer maxLimitWithdrawals, Double tax) {
        this.maxLimitWithdrawals = maxLimitWithdrawals;
        this.tax = tax;
    }

    @Override
    public double calculateWithdraw(double amount) {
        return amount + getTax();
    }
}
