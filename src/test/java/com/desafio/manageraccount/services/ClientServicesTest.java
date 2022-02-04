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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ClientServicesTest {

    private static final long INVALID_CLIENT_ID = 1L;
    private static final String INVALID_PHONE_NUMBER = "33a55554444";

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
    void whenFoundClientByCPF() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(expectedClient);

        assertEquals(clientService.clientByCPF(clientDTO),expectedClient);
    }

    @Test
    void whenNotFoundClientByCPFInformed() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.clientByCPF(clientDTO));
    }

    @Test
    void whenFoundClientByCNPJ() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ())).thenReturn(expectedClient);

        assertEquals(clientService.clientByCNPJ(clientDTO),expectedClient);
    }

    @Test
    void whenNotFoundClientByCNPJInformed() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        when(clientRepository.findByClientCPF(clientDTO.getClientCNPJ())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.clientByCNPJ(clientDTO));
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
    void whenPhoneNumberInformedIsInvalid() {
        ClientDTO clientDTO = new ClientDTO(1L,"Gabriel",INVALID_PHONE_NUMBER,"teste","126.251.926-82",  "59.144.503/0001-83");
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.save(expectedClient)).thenReturn(null);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientDTO));
    }

    @Test
    void whenNoInformedTheDocumentation() {
        ClientDTO clientDTO = new ClientDTO(1L,"Gabriel","33999995555","teste",null, null);
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.save(expectedClient)).thenReturn(null);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientDTO));
    }

    @Test
    void whenAlreadyRegisteredDocumentationInformed() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(expectedClient);
        when(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ())).thenReturn(expectedClient);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientDTO));
    }
    
    @Test
    void whenUpdateClientWithSuccess() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();
        ClientDTO newData = new ClientDTO(clientDTO.getId(),"Gabriel","22999995555","rua teste","938.447.860-15",  "01.120.328/0001-04");

        when(clientRepository.findByClientCPFAndClientCNPJ(clientDTO.getClientCPF(), clientDTO.getClientCNPJ())).thenReturn(expectedClient);
        when(clientRepository.save(expectedClient)).thenReturn(expectedClient);

        Client actualClient = clientService.updateClient(newData);
        assertEquals(actualClient.getName(),newData.getName());
        assertEquals(actualClient.getClientCPF(),newData.getClientCPF());
        assertEquals(actualClient.getClientCNPJ(),newData.getClientCNPJ());
        assertEquals(actualClient.getPhoneNumber(),newData.getPhoneNumber());
        assertEquals(actualClient.getAddress(),newData.getAddress());
    }


    @Test
    void whenDocumentationClientNotFoundForUpdate() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();

        when(clientRepository.findByClientCPFAndClientCNPJ(clientDTO.getClientCPF(), clientDTO.getClientCNPJ())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(clientDTO));
    }

    @Test
    void whenPhoneNumberForUpdateIsInvalid() {
        ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedClient = clientDTO.toDTO();
        ClientDTO invalidClient = new ClientDTO(clientDTO.getId(), "Gabriel",INVALID_PHONE_NUMBER,"teste","938.447.860-15",  "01.120.328/0001-04");

        when(clientRepository.findByClientCPFAndClientCNPJ(clientDTO.getClientCPF(), clientDTO.getClientCNPJ())).thenReturn(expectedClient);
        when(clientRepository.save(expectedClient)).thenReturn(expectedClient);

        assertThrows(DocumentationException.class, () -> clientService.updateClient(invalidClient));
    }

    @Test
    void whenDeleteClientWithSuccess() {
        ClientDTO clientDeleteDTO = ClientDTOBuilder.builder().build().toClientDTO();
        Client expectedDeleteClient = clientDeleteDTO.toDTO();

        when(clientRepository.findById(clientDeleteDTO.getId())).thenReturn(Optional.ofNullable(expectedDeleteClient));
        doNothing().when(clientRepository).deleteById(clientDeleteDTO.getId());

        clientService.deleteClientById(clientDeleteDTO.getId());

        verify(clientRepository, times(1)).findById(clientDeleteDTO.getId());
        verify(clientRepository, times(1)).deleteById(clientDeleteDTO.getId());
    }

    @Test
    void whenDeleteClientByIdIsNotExist() {
        when(clientRepository.findById(INVALID_CLIENT_ID)).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(INVALID_CLIENT_ID));
    }

}











