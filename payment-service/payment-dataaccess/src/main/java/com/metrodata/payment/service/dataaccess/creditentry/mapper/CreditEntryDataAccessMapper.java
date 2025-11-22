package com.metrodata.payment.service.dataaccess.creditentry.mapper;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import com.metrodata.payment.service.domain.core.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity){
        return CreditEntry.builder()
                .id(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry){
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

}
