package com.metrodata.order.service.domain.core.event;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.order.service.domain.core.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {

    public OrderCancelledEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }

}
