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

    public Client insertClient(ClientDTO clientDTO) {

        validateData(clientDTO);
        validatePhoneNumber(clientDTO);

        if (clientDTO.getClientCPF() != null && !Objects.isNull(clientRepository.findByClientCPF(clientDTO.getClientCPF()))) {
            throw new DocumentationException("O CPF já está cadastrado.");
        }
        if (clientDTO.getClientCNPJ() != null && !Objects.isNull(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ()))) {
            throw new DocumentationException("O CNPJ já está cadastrado.");
        }
        return clientRepository.save(clientDTO.toDTO());
    }

    public Client updateClient(ClientDTO clientDTO) {

        validateData(clientDTO);
        validatePhoneNumber(clientDTO);

        Client updateClient = clientIsExist(clientDTO);

        if (updateClient.getClientCNPJ() == null && clientDTO.getClientCNPJ() != null) {
            if (!Objects.isNull(clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ()))) {
                throw new DocumentationException("O CNPJ já está cadastrado.");
            }
            updateClient.setClientCNPJ(clientDTO.getClientCNPJ());
        }

        updateClient.setName(clientDTO.getName());
        updateClient.setAddress(clientDTO.getAddress());
        updateClient.setPhoneNumber(clientDTO.getPhoneNumber());

        return clientRepository.save(updateClient);
    }

    public void deleteClientById(Long id) {
        idIsExist(id);
        clientRepository.deleteById(id);
    }

    private Client clientIsExist(ClientDTO clientDTO) {
        Client client;
        if (clientDTO.getClientCPF() != null) {
            if (clientRepository.findByClientCPF(clientDTO.getClientCPF()) == null) {
                throw new ClientNotFoundException("Não foi encontrado o cliente com esse CPF: " + clientDTO.getClientCPF());
            }
            client = clientRepository.findByClientCPF(clientDTO.getClientCPF());
        }
        else {
            if (clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ()) == null) {
                throw new ClientNotFoundException("Não foi encontrado o cliente com esse CNPJ: " + clientDTO.getClientCNPJ());
            }
            client = clientRepository.findByClientCNPJ(clientDTO.getClientCNPJ());
        }
        return client;
    }

    private void validatePhoneNumber(ClientDTO clientDTO) {
        if (!clientDTO.getPhoneNumber().matches("^\\d+$")) {
            throw new DocumentationException("O telefone informado é inválido: " + clientDTO.getPhoneNumber());
        }
    }

    private void validateData(ClientDTO clientDTO) {
        if (clientDTO.getClientCPF() == null && clientDTO.getClientCNPJ() == null) {
            throw new DocumentationException("Erro: não foi informado um documento.");
        }
    }

    private void idIsExist(Long id) {
        clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(String.format("O cliente com esse id %d não foi encontrado", id)));
    }
}
