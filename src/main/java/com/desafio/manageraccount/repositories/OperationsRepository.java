package com.desafio.manageraccount.repositories;

import com.desafio.manageraccount.entities.Operations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationsRepository extends JpaRepository<Operations, Long> {

    List<Operations> findByAccountId(Long accountId);
    List<Operations> findByAccountDestinyAndAgencyDestinyAndDestinyVerifyDigit(String accountDestiny, String agencyDestiny, String destinyVerifyDigit);
}
