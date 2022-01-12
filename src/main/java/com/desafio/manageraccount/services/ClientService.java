package com.desafio.manageraccount.services;

import com.desafio.manageraccount.entities.Client;
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

    public Client returnOneClient (Long id) {
        Client client = idIsExist(id);
        return client;
    }

    public Client insertClient(Client client) {
        Client newCliente = clientRepository.save(client);
        return newCliente;
    }

    public void delete(Long id) {
        idIsExist(id);
        clientRepository.deleteById(id);
    }

    public Client updateClient(Long id, Client client) {
        idIsExist(id);
        Client updateClient = clientRepository.getById(id);
        updateClient.setClientCPF(client.getClientCPF());
        updateClient.setName(client.getName());
        updateClient.setAddress(client.getAddress());
        updateClient.setFoneNumber(client.getFoneNumber());

        clientRepository.save(updateClient);

        return updateClient;
    }

    public Client clientById(Long id) {
        Client client = clientRepository.findById(id).get();
        return client;
    }

    private Client idIsExist(Long id) {
      return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }
}
