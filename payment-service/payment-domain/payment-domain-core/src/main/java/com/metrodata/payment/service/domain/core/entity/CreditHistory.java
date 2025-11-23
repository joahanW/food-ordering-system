package com.metrodata.payment.service.domain.core.entity;

import com.metrodata.common.domain.entity.BaseEntity;
import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.payment.service.domain.core.valueobject.CreditHistoryId;
import com.metrodata.payment.service.domain.core.valueobject.TransactionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreditHistory extends BaseEntity<CreditHistoryId> {

    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

}
