package com.metrodata.payment.service.dataaccess.creditentry.adapter;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.metrodata.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.CreditEntryRepository;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(
                creditEntryJpaRepository.save(
                        creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)
                )
        );
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository.findByCustomerId(customerId.getValue())
                .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
    }
}
