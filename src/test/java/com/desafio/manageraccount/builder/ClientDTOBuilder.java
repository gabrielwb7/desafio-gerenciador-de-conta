package com.desafio.manageraccount.builder;

import com.desafio.manageraccount.dto.request.ClientDTO;
import lombok.Builder;

@Builder
public class ClientDTOBuilder {

    @Builder.Default
    private String name = "Gabriel";
    @Builder.Default
    private String phoneNumber = "34988887777";
    @Builder.Default
    private String address = "Rua Brasil 555";
    @Builder.Default
    private String clientCPF = "126.251.926-82";
    @Builder.Default
    private String clienteCNPJ = "01.120.328/0001-04";

    public ClientDTO toClientDTO () {
        return new ClientDTO(name,phoneNumber,address,clientCPF,clienteCNPJ);
    }
}
