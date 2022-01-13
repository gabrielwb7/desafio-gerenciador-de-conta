package com.desafio.manageraccount.exceptions;

public class ClientAlreadyRegisteredException extends RuntimeException{
    public ClientAlreadyRegisteredException(String msg) {
        super(msg);
    }
}
