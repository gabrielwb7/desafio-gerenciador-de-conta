package com.desafio.manageraccount.dto.request;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import com.desafio.manageraccount.utils.Validate;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Null;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO extends Validate {

    @Enumerated(EnumType.STRING)
    private TypeAccount typeAccount;
    private String agency;
    private String numberAccount;
    private String verifyDigit;

    public Account toDTO () {
        return new Account(agency, numberAccount, typeAccount, verifyDigit);
    }

}

