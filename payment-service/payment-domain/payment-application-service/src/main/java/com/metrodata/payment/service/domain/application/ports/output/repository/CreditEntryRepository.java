package com.metrodata.payment.service.domain.application.ports.output.repository;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;

import java.util.Optional;
import java.util.UUID;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);
    Optional<CreditEntry> findByCustomerId(CustomerId customerId);

}
