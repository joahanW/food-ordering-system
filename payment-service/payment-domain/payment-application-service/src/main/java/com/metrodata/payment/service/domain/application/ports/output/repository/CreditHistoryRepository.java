package com.metrodata.payment.service.domain.application.ports.output.repository;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save (CreditHistory creditHistory);
    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);

}
