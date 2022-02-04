package com.desafio.manageraccount.dto.response.responsesoperations;

import com.desafio.manageraccount.entities.Operations;
import com.desafio.manageraccount.entities.enums.TypeOperations;
import com.desafio.manageraccount.entities.enums.TypeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@AllArgsConstructor
public class ResponseWithdraw {

    private Long idOperation;
    @Enumerated(EnumType.STRING)
    private TypeOperations typeOperations;
    private String dateOperation;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TypeStatus typeStatus;
    private Double tax;

    public static ResponseWithdraw responseWithdraw(Operations operations) {
        return new ResponseWithdraw(operations.getIdOperation(), operations.getTypeOperations(), operations.getDateOperation(), operations.getAmount(), operations.getTypeStatus(), operations.getTax());
    }
}
