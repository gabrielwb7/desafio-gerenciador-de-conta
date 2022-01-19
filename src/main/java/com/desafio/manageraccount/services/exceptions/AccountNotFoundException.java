package com.desafio.manageraccount.services.exceptions;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException (Long id) {
        super("Conta com ID " + id + " não encontrado");
    }

}