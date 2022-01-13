package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.BankingOperations;
import com.desafio.manageraccount.entities.response.MessageResponse;
import com.desafio.manageraccount.exceptions.BankingOperationsNotFound;
import com.desafio.manageraccount.repositories.BankingOperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankingOperationsServices {

    @Autowired
    private final BankingOperationsRepository bankingOperationsRepository;

    public BankingOperationsServices(BankingOperationsRepository bankingOperationsRepository) {
        this.bankingOperationsRepository = bankingOperationsRepository;
    }

    public List<BankingOperations> operationsList() {
        List<BankingOperations> operations = bankingOperationsRepository.findAll();
        return operations;
    }

    public MessageResponse insertOperation(BankingOperations operation) {
        BankingOperations newOperation = bankingOperationsRepository.save(operation);
        return createMessageResponse(String.format("Cliente com o ID %d foi criado com sucesso", newOperation.getIdOperation()));
    }

    public BankingOperations operationById(Long id) {
        idIsExist(id);
        BankingOperations operation = bankingOperationsRepository.findById(id).get();
        return operation;
    }

    private BankingOperations idIsExist(Long id) {
        return bankingOperationsRepository.findById(id).orElseThrow(() -> new BankingOperationsNotFound(id));
    }


    private MessageResponse createMessageResponse (String textMessage) {
        return MessageResponse.builder().message(textMessage).build();
    }
}
