package com.metrodata.restaurant.service.domain.core;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.common.domain.valueobject.OrderApprovalStatus;
import com.metrodata.restaurant.service.domain.core.entity.OrderApproval;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovalEvent;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovedEvent;
import com.metrodata.restaurant.service.domain.core.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.metrodata.common.domain.DomainConstant.UTC;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {
    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant,
                                            List<String> failureMessages) {
        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}",restaurant.getOrderDetail().getId());

        if (failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)));
        }else {
            log.info("Order is rejected for order id :{}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC))
            );
        }
    }
}
