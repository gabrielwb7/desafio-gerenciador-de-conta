package com.desafio.manageraccount.services.exceptions;

public class CouldNotCompleteTheRequest extends RuntimeException{
    public CouldNotCompleteTheRequest(String msg) {
        super(msg);
    }
}
