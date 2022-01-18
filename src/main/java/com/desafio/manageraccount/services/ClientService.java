package com.desafio.manageraccount.services;

import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.dto.response.MessageResponse;
import com.desafio.manageraccount.exceptions.ClientAlreadyRegisteredException;
import com.desafio.manageraccount.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClientService {

    @Autowired
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> listAllClients() {
        List<Client> allClients = clientRepository.findAll();
        return allClients;
    }

    public MessageResponse insertClient(ClientDTO clientDTO) {
        if (!Objects.isNull(clientRepository.getClientByClientCPF(clientDTO.getClientCPF()))) {
            throw new ClientAlreadyRegisteredException("O CPF já está cadastrado.");
        }
        Client newClient = clientRepository.save(clientDTO.toDTO());
        return createMessageResponse(String.format("Cliente com o ID %d foi criado com sucesso", newClient.getId()));
    }

    public void deleteClientById(Long id) {
        idIsExist(id);
        clientRepository.deleteById(id);
    }

    public MessageResponse updateClient(Long id, ClientDTO clientDTO) {
        idIsExist(id);
        Client updateClient = clientRepository.getById(id);
        updateClient.setName(clientDTO.getName());
        updateClient.setAddress(clientDTO.getAddress());
        updateClient.setPhoneNumber(clientDTO.getPhoneNumber());

        clientRepository.save(updateClient);

        return createMessageResponse(String.format("Cliente com o ID %d foi atualizado", updateClient.getId()));
    }

    public Client clientById(Long id) {
        idIsExist(id);
        Client client = clientRepository.findById(id).get();
        return client;
    }

    private Client idIsExist(Long id) {
      return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    private MessageResponse createMessageResponse (String textMessage) {
        return MessageResponse.builder().message(textMessage).build();
    }
}
