package com.desafio.manageraccount.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOperationExceptions extends RuntimeException{
    public InvalidOperationExceptions(String msg) {
        super(msg);
    }
}
