package com.desafio.manageraccount.dto.request;

import com.desafio.manageraccount.entities.Client;
import lombok.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    @CPF
    private String clientCPF;
    @CNPJ
    private String clientCNPJ;


    public Client toDTO() {
        return new Client(name, clientCPF, clientCNPJ, phoneNumber, address);
    }

}
