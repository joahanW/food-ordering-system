package com.metrodata.payment.service.domain.application.ports.output.message.publisher;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
