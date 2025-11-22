package com.metrodata.payment.service.domain.core.entity;

import com.metrodata.common.domain.entity.BaseEntity;
import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.payment.service.domain.core.valueobject.CreditEntryId;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
public class CreditEntry extends BaseEntity<CreditEntryId> {

    private final CustomerId customerId;
    private Money totalCreditAmount;

    public void addCreditAmount(Money money){
        totalCreditAmount = totalCreditAmount.add(money);
    }

    public void subtractCreditAmount(Money money){
        totalCreditAmount = totalCreditAmount.subtract(money);
    }

}
