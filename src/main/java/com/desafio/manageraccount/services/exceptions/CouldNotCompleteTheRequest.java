package com.desafio.manageraccount.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class CouldNotCompleteTheRequest extends RuntimeException{
    public CouldNotCompleteTheRequest(String msg) {
        super(msg);
    }
}
