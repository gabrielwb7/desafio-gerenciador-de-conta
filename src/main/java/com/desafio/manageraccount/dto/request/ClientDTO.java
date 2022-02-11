package com.desafio.manageraccount.dto.request;

import com.desafio.manageraccount.entities.Client;
import lombok.*;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

@Data
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

    public ClientDTO(String name, String phoneNumber, String address, String clientCPF, String clientCNPJ) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.clientCPF = clientCPF;
        this.clientCNPJ = clientCNPJ;
    }

    public Client toDTO() {
        return new Client(name, clientCPF, clientCNPJ, phoneNumber, address);
    }

}
