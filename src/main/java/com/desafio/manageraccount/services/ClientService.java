package com.desafio.manageraccount.services;

import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.repositories.ClientRepository;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
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

    public Client insertClient(ClientDTO clientDTO) {

        if (clientDTO.getClientCPF() == null && clientDTO.getClientCNPJ() == null) {
            throw new DocumentationException("Você precisa informar um CPF ou CNPJ para efetuar o cadastro");
        }
        if (!Objects.isNull(clientRepository.getClientByClientCPF(clientDTO.getClientCPF()))) {
            throw new DocumentationException("O CPF já está cadastrado.");
        }
        if (clientDTO.getClientCNPJ() != null) {
            if (!Objects.isNull(clientRepository.getClientByClientCNPJ(clientDTO.getClientCNPJ()))) {
                throw new DocumentationException("O CNPJ já está cadastrado.");
            }
        }
        return  clientRepository.save(clientDTO.toDTO());
    }

    public void deleteClientById(Long id) {
        idIsExist(id);
        clientRepository.deleteById(id);
    }

    public Client updateClient(Long id, ClientDTO clientDTO) {
        idIsExist(id);

        Client updateClient = clientRepository.getById(id);
        updateClient.setName(clientDTO.getName());
        updateClient.setAddress(clientDTO.getAddress());
        updateClient.setPhoneNumber(clientDTO.getPhoneNumber());

        return clientRepository.save(updateClient);
    }

    public Client clientById(Long id) {
        idIsExist(id);
        Client client = clientRepository.findById(id).get();
        return client;
    }

    private Client idIsExist(Long id) {
      return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }
}
