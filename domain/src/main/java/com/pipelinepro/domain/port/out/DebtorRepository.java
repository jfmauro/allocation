package com.pipelinepro.domain.port.out;

import com.pipelinepro.domain.Debtor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebtorRepository {
    Optional<Debtor> findById(UUID debtorId);

    Optional<Debtor> findByNationalNumberHash(String nationalNumberHash);

    Optional<Debtor> findByEnterpriseNumber(String enterpriseNumber);

    List<Debtor> findAllActive();
}
