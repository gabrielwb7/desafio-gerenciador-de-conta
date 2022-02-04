package com.desafio.manageraccount.dto.response.responsesoperations;

import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@AllArgsConstructor
@Getter
public class ResponseDeposit {

    private Long idOperation;
    @Enumerated(EnumType.STRING)
    private TypeOperations typeOperations;
    private String dateOperation;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TypeStatus typeStatus;

    public static ResponseDeposit responseDeposit(Operations operations) {
        return new ResponseDeposit(operations.getIdOperation(), operations.getTypeOperations(), operations.getDateOperation(), operations.getAmount(), operations.getTypeStatus());
    }
}
