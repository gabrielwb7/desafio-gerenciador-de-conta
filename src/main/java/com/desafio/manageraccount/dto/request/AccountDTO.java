package com.desafio.manageraccount.dto.request;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeAccount typeAccount;
    private String agency;

    public Account toDTO () {
        return new Account(agency, typeAccount);
    }

}

