package com.metrodata.restaurant.service.domain.core.event;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.restaurant.service.domain.core.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent{

    public OrderRejectedEvent(OrderApproval orderApproval, RestaurantId restaurantId, List<String> failureMessages, ZonedDateTime createdAt) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
    }
}
