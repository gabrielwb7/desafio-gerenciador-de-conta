package com.desafio.manageraccount.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BankingOperationsNotFound extends RuntimeException {

    public BankingOperationsNotFound (Long id) {
        super("Operação com ID " + id + " não encontrado");
    }

}
