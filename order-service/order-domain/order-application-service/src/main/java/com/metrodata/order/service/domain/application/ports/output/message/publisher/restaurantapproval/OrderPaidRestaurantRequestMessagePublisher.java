package com.metrodata.order.service.domain.application.ports.output.message.publisher.restaurantapproval;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.order.service.domain.core.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
