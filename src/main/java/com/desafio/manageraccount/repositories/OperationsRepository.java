package com.desafio.manageraccount.repositories;

import com.desafio.manageraccount.entities.Operations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationsRepository extends JpaRepository<Operations, Long> {}
