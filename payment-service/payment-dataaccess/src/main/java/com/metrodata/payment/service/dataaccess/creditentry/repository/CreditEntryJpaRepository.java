package com.metrodata.payment.service.dataaccess.creditentry.repository;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditEntryJpaRepository extends JpaRepository<CreditEntryEntity, Long> {
    Optional<CreditEntryEntity> findByCustomerId(UUID customerId);
}
