package com.desafio.manageraccount.services;

import com.desafio.manageraccount.builder.ClientDTOBuilder;
import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.repositories.ClientRepository;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ClientServicesTest {

    private static final long INVALID_CLIENT_ID = 1L;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void whenListClientsIsCalledThenReturnAnEmptyListOfClients() {

        when(clientRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<Client> foundListClient = clientService.listAllClients();

        assertThat(foundListClient, Matchers.is((empty())));
    }

    @Test
    void whenListClientsIsCalledThenReturnListOfClients() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findAll()).thenReturn(Collections.singletonList(expectedClient));

        List<Client> foundListClient = clientService.listAllClients();

        assertThat(foundListClient, Matchers.is(Matchers.not(empty())));
        assertThat(foundListClient.get(0), Matchers.is(Matchers.equalTo(expectedClient)));
    }

    @Test
    void whenClientInformedThenItShouldBeCreated() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(null);
        when(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ())).thenReturn(null);
        when(clientRepository.save(expectedClient)).thenReturn(expectedClient);

        Client createClient = clientService.insertClient(clientDTO);

        assertEquals(clientDTO.getName(),createClient.getName());
        assertEquals(clientDTO.getClientCPF(),createClient.getClientCPF());
        assertEquals(clientDTO.getClientCNPJ(),createClient.getClientCNPJ());
        assertEquals(clientDTO.getPhoneNumber(),createClient.getPhoneNumber());
        assertEquals(clientDTO.getAddress(),createClient.getAddress());
    }

    @Test
    void whenAlreadyRegisteredDocumentationInformed() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(expectedClient);
        when(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ())).thenReturn(expectedClient);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientDTO));
    }

//    @Test
//    void whenUpdateClientByIdIsNoExist() {}

    @Test
    void whenDeleteClientByIdIsNotExist() {
        when(clientRepository.findById(INVALID_CLIENT_ID)).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(INVALID_CLIENT_ID));
    }

}











