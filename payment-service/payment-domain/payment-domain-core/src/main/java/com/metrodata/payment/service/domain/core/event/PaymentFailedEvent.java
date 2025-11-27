package com.metrodata.payment.service.domain.core.event;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.payment.service.domain.core.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {

    public PaymentFailedEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessage) {
        super(payment, createdAt, failureMessage);
    }
}
