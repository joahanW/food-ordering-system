package com.metrodata.payment.service.dataaccess.credithistory.adapter;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.metrodata.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.metrodata.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.CreditHistoryRepository;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(
                creditHistoryJpaRepository.save(
                        creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)
                )
        );
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        Optional<List<CreditHistoryEntity>> creditHistory
                = creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
        return creditHistory
                .map(creditHistoryList -> creditHistoryList.stream()
                        .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
                        .toList());
    }
}
