package com.metrodata.payment.service.domain.application.ports.output.message.publisher;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.payment.service.domain.core.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
