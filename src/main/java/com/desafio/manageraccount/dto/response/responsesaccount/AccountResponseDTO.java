package com.desafio.manageraccount.dto.response.responsesaccount;

import com.desafio.manageraccount.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountResponseDTO {

    private String agency;
    private String numberAccount;
    private String verifyDigit;

    public static AccountResponseDTO toDTO (Account account) {
        return new AccountResponseDTO(account.getAgency(), account.getNumberAccount(), account.getVerifyDigit());
    }

}
