package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Account;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.entities.response.MessageResponse;
import com.desafio.manageraccount.exceptions.AccountAlreadyRegisteredException;
import com.desafio.manageraccount.exceptions.ClientAlreadyRegisteredException;
import com.desafio.manageraccount.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public MessageResponse insertClient(Client client) {
        theCPFIsRegistered(client);
        Client newCliente = clientRepository.save(client);
        return createMessageResponse(String.format("Cliente com o ID %d foi criado com sucesso", newCliente.getId()));
    }

    public void deleteClientById(Long id) {
        idIsExist(id);
        clientRepository.deleteById(id);
    }

    public MessageResponse updateClient(Long id, Client client) {
        idIsExist(id);
        Client updateClient = clientRepository.getById(id);
        updateClient.setName(client.getName());
        updateClient.setAddress(client.getAddress());
        updateClient.setFoneNumber(client.getFoneNumber());

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

    private void theCPFIsRegistered(Client newClient) {
        List<Client> allClients = clientRepository.findAll();
        for (Client client : allClients) {
            if (client.equals(newClient)) {
                throw new ClientAlreadyRegisteredException(String.format("O CPF %s informado já está cadastrado", newClient.getClientCPF()));
            }
        }
    }

    private MessageResponse createMessageResponse (String textMessage) {
        return MessageResponse.builder().message(textMessage).build();
    }
}
