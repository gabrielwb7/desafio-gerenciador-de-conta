package com.desafio.manageraccount.services.exceptions;

public class BankingOperationsNotFound extends RuntimeException {

    public BankingOperationsNotFound (Long id) {
        super("Operação com ID " + id + " não encontrado");
    }

}
