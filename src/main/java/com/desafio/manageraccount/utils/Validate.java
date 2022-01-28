package com.desafio.manageraccount.utils;

import com.desafio.manageraccount.services.exceptions.DocumentationException;

public abstract class Validate {

    public final void validatesDataAccount(String agency, String numberAccount, String verifyDigit) {
        boolean validate = agency.matches("^\\d+$") && numberAccount.matches("^\\d+$") && verifyDigit.matches("^\\d+$");
        if (!validate) {
            throw new DocumentationException("Os dados informados da conta estão inválidos: "
                    + "agency - " + agency
                    + ", account - " + numberAccount
                    + ", verify digit - " + verifyDigit);
        }
    }

    public final void validatePhoneNumber(String phoneNumber) {
        boolean validate = phoneNumber.matches("^\\d+$");
        if (!validate) {
            throw new DocumentationException("Os dados informados da conta estão inválidos: " + phoneNumber);
        }
    }
}
