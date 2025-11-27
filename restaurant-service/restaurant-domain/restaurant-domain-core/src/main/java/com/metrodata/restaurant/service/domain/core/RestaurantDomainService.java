package com.metrodata.restaurant.service.domain.core;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovalEvent;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovedEvent;
import com.metrodata.restaurant.service.domain.core.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages);

}
