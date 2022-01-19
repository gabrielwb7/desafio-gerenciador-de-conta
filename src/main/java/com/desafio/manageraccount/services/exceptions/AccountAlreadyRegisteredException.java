package com.desafio.manageraccount.services.exceptions;

public class AccountAlreadyRegisteredException extends RuntimeException {

    public AccountAlreadyRegisteredException(String msg) {
        super(msg);
    }
}
