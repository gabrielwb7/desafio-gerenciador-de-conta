package com.desafio.manageraccount.entities.enums;

import lombok.Getter;

@Getter
public enum TypeAccount {

    LEGALPERSON(50, 10.0),
    REGULARPERSON(5, 10.0),
    GOVERNMENTPERSON(250, 20.0);

    private Integer maxLimitWithdrawals;
    private Double tax;

    TypeAccount(Integer maxLimitWithdrawals, Double tax) {
        this.maxLimitWithdrawals = maxLimitWithdrawals;
        this.tax = tax;
    }

}
