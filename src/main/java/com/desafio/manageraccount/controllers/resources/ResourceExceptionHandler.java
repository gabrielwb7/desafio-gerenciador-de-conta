package com.desafio.manageraccount.controllers.resources;

import com.desafio.manageraccount.services.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {


    @ExceptionHandler(DocumentationException.class)
    public ResponseEntity<StandardError> DocumentationError(DocumentationException e, HttpServletRequest request) {
        String error = "Documentation error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccountAlreadyRegisteredException.class)
    public ResponseEntity<StandardError> accountAlreadyRegistered(AccountAlreadyRegisteredException e, HttpServletRequest request) {
        String error = "Registered error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<StandardError> accountNotFound(AccountNotFoundException e, HttpServletRequest request) {
        String error = "Account not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BankingOperationsNotFound.class)
    public ResponseEntity<StandardError> operationsNotFound(BankingOperationsNotFound e, HttpServletRequest request) {
        String error = "Operation not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<StandardError> clientNotFound(ClientNotFoundException e, HttpServletRequest request) {
        String error = "Client not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InvalidOperationExceptions.class)
    public ResponseEntity<StandardError> operationError(InvalidOperationExceptions e, HttpServletRequest request) {
        String error = "Operation error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(CouldNotCompleteTheRequest.class)
    public ResponseEntity<StandardError> kafkaError(CouldNotCompleteTheRequest e, HttpServletRequest request) {
        String error = "Services error";
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> invalidDocument(MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Documentation error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardError> invalidSize(ConstraintViolationException e, HttpServletRequest request) {
        String error = "Size error";

        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
