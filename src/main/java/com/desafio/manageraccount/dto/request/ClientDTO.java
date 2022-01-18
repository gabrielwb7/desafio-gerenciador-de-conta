package com.desafio.manageraccount.dto.request;

import com.desafio.manageraccount.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {

    private String name;
    private String phoneNumber;
    private String address;
    @CPF
    private String clientCPF;


    public Client toDTO() {
        return new Client(name, clientCPF, phoneNumber, address);
    }

}
