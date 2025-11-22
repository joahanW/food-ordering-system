package com.metrodata.payment.service.dataaccess.credithistory.repository;

import com.metrodata.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditHistoryJpaRepository extends JpaRepository<CreditHistoryEntity, UUID> {

    Optional<List<CreditHistoryEntity>> findByCustomerId(UUID customerId);

}
