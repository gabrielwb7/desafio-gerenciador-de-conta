package com.desafio.manageraccount.repositories;

import com.desafio.manageraccount.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client getClientByClientCPF (String clientCPF);
}
