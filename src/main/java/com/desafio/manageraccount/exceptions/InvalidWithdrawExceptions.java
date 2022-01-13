package com.desafio.manageraccount.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWithdrawExceptions extends RuntimeException{
    public InvalidWithdrawExceptions(String msg) {
        super(msg);
    }
}
