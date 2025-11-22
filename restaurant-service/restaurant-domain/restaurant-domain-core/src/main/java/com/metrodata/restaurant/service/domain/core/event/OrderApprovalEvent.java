package com.metrodata.restaurant.service.domain.core.event;

import com.metrodata.common.domain.event.DomainEvent;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.restaurant.service.domain.core.entity.OrderApproval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {
    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;
}
