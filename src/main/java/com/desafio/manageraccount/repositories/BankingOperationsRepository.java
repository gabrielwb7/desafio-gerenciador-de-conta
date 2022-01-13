package com.desafio.manageraccount.repositories;

import com.desafio.manageraccount.entities.BankingOperations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankingOperationsRepository extends JpaRepository<BankingOperations, Long> {}
