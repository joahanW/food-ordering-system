package com.metrodata.payment.service.domain.core.valueobject;

import com.metrodata.common.domain.valueobject.BaseId;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
