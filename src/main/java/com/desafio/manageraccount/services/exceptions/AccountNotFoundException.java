package com.desafio.manageraccount.services.exceptions;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException ( String msg) {
        super(msg);
    }

}
