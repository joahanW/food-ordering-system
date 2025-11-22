package com.metrodata.payment.service.domain.core.event;

import com.metrodata.common.domain.event.DomainEvent;
import com.metrodata.payment.service.domain.core.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
public abstract class PaymentEvent implements DomainEvent<Payment> {

    private final Payment payment;
    private final ZonedDateTime createdAt;
    private final List<String> failureMessage;

}
