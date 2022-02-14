package com.desafio.manageraccount.builder;

import com.desafio.manageraccount.dto.request.AccountDTO;
import com.desafio.manageraccount.entities.enums.TypeAccount;
import lombok.Builder;

@Builder
public class AccountDTOBuilder {

    @Builder.Default
    private Long id = 1L;
    @Builder.Default
    private TypeAccount typeAccount = TypeAccount.REGULARPERSON;
    @Builder.Default
    private String agency = "2222";


    public AccountDTO accountDTO () {
        return new AccountDTO(id,typeAccount,agency);
    }
}
