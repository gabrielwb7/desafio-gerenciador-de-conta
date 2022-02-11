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
    private static final ClientDTO clientDTO = ClientDTOBuilder.builder().build().toClientDTO();

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
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findAll()).thenReturn(Collections.singletonList(expectedClient));

        List<Client> foundListClient = clientService.listAllClients();

        assertThat(foundListClient, Matchers.is(Matchers.not(empty())));
        assertThat(foundListClient.get(0), Matchers.is(Matchers.equalTo(expectedClient)));
    }

    @Test
    void whenFoundClientByCPF() {
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(expectedClient);

        assertEquals(clientService.clientByCPF(clientDTO),expectedClient);
    }

    @Test
    void whenNotFoundClientByCPFInformed() {
        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.clientByCPF(clientDTO));
    }

    @Test
    void whenFoundClientByCNPJ() {
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ())).thenReturn(expectedClient);

        assertEquals(clientService.clientByCNPJ(clientDTO),expectedClient);
    }

    @Test
    void whenNotFoundClientByCNPJInformed() {
        when(clientRepository.findByClientCPF(clientDTO.getClientCNPJ())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.clientByCNPJ(clientDTO));
    }

    @Test
    void whenClientInformedThenItShouldBeCreated() {
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
        ClientDTO clientTempDTO = new ClientDTO("Gabriel",INVALID_PHONE_NUMBER,"teste","126.251.926-82",  "59.144.503/0001-83");
        Client expectedClient = clientTempDTO.toDTO();

        when(clientRepository.save(expectedClient)).thenReturn(null);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientTempDTO));
    }

    @Test
    void whenNoInformedTheDocumentation() {
        ClientDTO clientTempDTO = new ClientDTO("Gabriel","33999995555","teste",null, null);
        Client expectedClient = clientTempDTO.toDTO();

        when(clientRepository.save(expectedClient)).thenReturn(null);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientTempDTO));
    }

    @Test
    void whenAlreadyRegisteredCPFInformed() {
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(expectedClient);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientDTO));
    }

    @Test
    void whenAlreadyRegisteredCNPJInformed() {
        Client expectedClient = clientDTO.toDTO();

        when(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ())).thenReturn(expectedClient);

        assertThrows(DocumentationException.class, () -> clientService.insertClient(clientDTO));
    }

    @Test
    void whenUpdateClientWithSuccess() {
        Client expectedClient = clientDTO.toDTO();
        ClientDTO newData = new ClientDTO("Gabriel","22999995555","rua teste","938.447.860-15",  "01.120.328/0001-04");

        when(clientRepository.findByClientCPF(expectedClient.getClientCPF())).thenReturn(expectedClient);
        when(clientRepository.save(expectedClient)).thenReturn(expectedClient);

        Client actualClient = clientService.updateClient(newData);
        assertEquals(actualClient.getName(),newData.getName());
        assertEquals(actualClient.getClientCPF(),newData.getClientCPF());
        assertEquals(actualClient.getClientCNPJ(),newData.getClientCNPJ());
        assertEquals(actualClient.getPhoneNumber(),newData.getPhoneNumber());
        assertEquals(actualClient.getAddress(),newData.getAddress());
    }

    @Test
    void whenUpdateClientWithCNPJ() {
        ClientDTO clientTemp = new ClientDTO("Randel", "33444445555", "Rua Almeida 55", "938.447.860-15", null);
        Client client = clientTemp.toDTO();

        when(clientRepository.findByClientCPF(client.getClientCPF())).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        Client actualClient = clientService.updateClient(clientDTO);

        assertEquals(actualClient.getName(),clientDTO.getName());
        assertEquals(actualClient.getClientCPF(),clientDTO.getClientCPF());
        assertEquals(actualClient.getClientCNPJ(),clientDTO.getClientCNPJ());
        assertEquals(actualClient.getPhoneNumber(),clientDTO.getPhoneNumber());
        assertEquals(actualClient.getAddress(),clientDTO.getAddress());

    }

    @Test
    void whenUpdateClientWithOnlyCNPJValid() {
        ClientDTO clientTemp = new ClientDTO("Randel", "33444445555", "Rua Almeida 55", null, "01.120.328/0001-04");
        Client client = clientTemp.toDTO();

        ClientDTO clientTempDTO = new ClientDTO("Gabriel", "11222223333","teste", null , "01.120.328/0001-04");

        when(clientRepository.findByClientCNPJ(client.getClientCNPJ())).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        Client actualClient = clientService.updateClient(clientTempDTO);

        assertEquals(actualClient.getName(),clientTempDTO.getName());
        assertEquals(actualClient.getClientCPF(),clientTempDTO.getClientCPF());
        assertEquals(actualClient.getClientCNPJ(),clientTempDTO.getClientCNPJ());
        assertEquals(actualClient.getPhoneNumber(),clientTempDTO.getPhoneNumber());
        assertEquals(actualClient.getAddress(),clientTempDTO.getAddress());

    }

    @Test
    void whenUpdateClientWithOnlyCNPJIsNotExist() {
        ClientDTO clientTemp = new ClientDTO("Randel", "33444445555", "Rua Almeida 55", null, "01.120.328/0001-04");

        when(clientRepository.findByClientCNPJ(clientTemp.getClientCNPJ())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(clientTemp));
    }

    @Test
    void whenTheClientInformedACNPJAlreadyRegistered () {
        Client otherClient = new Client("Randel", "085.385.470-03", "01.120.328/0001-04", "33444445555", "Rua Almeida 55");
        Client client = new Client("Tolkien", "938.447.860-15", null, "22888884444", "Rua Modor 111");

        when(clientRepository.findByClientCPF(client.getClientCPF())).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientRepository.findByClientCNPJ(otherClient.getClientCNPJ())).thenReturn(otherClient);
        when(clientRepository.save(otherClient)).thenReturn(otherClient);

        assertThrows(DocumentationException.class, () -> clientService.updateClient(clientDTO));
    }

    @Test
    void whenDocumentationClientNotFoundForUpdate() {

        when(clientRepository.findByClientCPF(clientDTO.getClientCPF())).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(clientDTO));
    }

    @Test
    void whenPhoneNumberForUpdateIsInvalid() {
        Client expectedClient = clientDTO.toDTO();
        ClientDTO invalidClient = new ClientDTO("Gabriel",INVALID_PHONE_NUMBER,"teste","938.447.860-15",  "01.120.328/0001-04");

        when(clientRepository.findById(expectedClient.getId())).thenReturn(Optional.of(expectedClient));
        when(clientRepository.save(expectedClient)).thenReturn(expectedClient);

        assertThrows(DocumentationException.class, () -> clientService.updateClient(invalidClient));
    }

    @Test
    void whenDeleteClientWithSuccess() {
        Client expectedDeleteClient = clientDTO.toDTO();

        when(clientRepository.findById(clientDTO.getId())).thenReturn(Optional.ofNullable(expectedDeleteClient));
        doNothing().when(clientRepository).deleteById(clientDTO.getId());

        clientService.deleteClientById(clientDTO.getId());

        verify(clientRepository, times(1)).findById(clientDTO.getId());
        verify(clientRepository, times(1)).deleteById(clientDTO.getId());
    }

    @Test
    void whenDeleteClientByIdIsNotExist() {
        when(clientRepository.findById(INVALID_CLIENT_ID)).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(INVALID_CLIENT_ID));
    }

}











