package com.desafio.manageraccount.services;

import com.desafio.manageraccount.dto.request.ClientDTO;
import com.desafio.manageraccount.entities.Client;
import com.desafio.manageraccount.repositories.ClientRepository;
import com.desafio.manageraccount.services.exceptions.ClientNotFoundException;
import com.desafio.manageraccount.services.exceptions.DocumentationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> listAllClients() {
        return clientRepository.findAll();
    }

    public Client insertClient(ClientDTO clientDTO) {

        clientDTO.validatePhoneNumber(clientDTO.getPhoneNumber());

        if (clientDTO.getClientCPF() == null && clientDTO.getClientCNPJ() == null) {
            throw new DocumentationException("Você precisa informar um CPF ou CNPJ para efetuar o cadastro");
        }
        if (!Objects.isNull(clientRepository.findByClientCPF(clientDTO.getClientCPF()))) {
            throw new DocumentationException("O CPF já está cadastrado.");
        }
        if (!Objects.isNull(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ()))) {
            throw new DocumentationException("O CNPJ já está cadastrado.");
        }

        return  clientRepository.save(clientDTO.toDTO());
    }

    public void deleteClientById(Long id) {
        idIsExist(id);
        clientRepository.deleteById(id);
    }

    public Client updateClient(Long id, ClientDTO clientDTO) {
        idIsExist(id);
        clientDTO.validatePhoneNumber(clientDTO.getPhoneNumber());

        Client updateClient = clientRepository.getById(id);
        updateClient.setName(clientDTO.getName());
        updateClient.setAddress(clientDTO.getAddress());
        updateClient.setPhoneNumber(clientDTO.getPhoneNumber());

        return clientRepository.save(updateClient);
    }

    public Client clientByCPF(ClientDTO clientDTO) {
        Client client = clientRepository.findByClientCPF(clientDTO.getClientCPF());
        if (client == null) {
            throw new ClientNotFoundException("Não foi encontrado na base de dados cliente com esse CPF: " + clientDTO.getClientCPF());
        }

        return client;
    }

    public Client clientByCNPJ(ClientDTO clientDTO) {
        Client client = clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ());
        if (client == null) {
            throw new ClientNotFoundException("Não foi encontrado na base de dados cliente com esse CNPJ: " + clientDTO.getClientCNPJ());
        }
        return client;
    }

    private void idIsExist(Long id) {
        clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(String.format("O cliente com esse id %d não foi encontrado", id)));
    }
}
