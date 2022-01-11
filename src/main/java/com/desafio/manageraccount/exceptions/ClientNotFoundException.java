package com.desafio.manageraccount.exceptions;

public class ClientNotFoundException extends RuntimeException{

    public ClientNotFoundException(Long id) {
        super("Cliente com ID " + id + " não encontrado");
    }
}
