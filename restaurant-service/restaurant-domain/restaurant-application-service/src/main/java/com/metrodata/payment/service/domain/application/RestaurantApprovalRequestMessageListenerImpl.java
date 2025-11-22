package com.metrodata.payment.service.domain.application;

import com.metrodata.payment.service.domain.application.dto.RestaurantApprovalRequest;
import com.metrodata.payment.service.domain.application.mapper.RestaurantDataMapper;
import com.metrodata.payment.service.domain.application.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {

    private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;

    @Override
    public void approvedOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        OrderApprovalEvent orderApprovalEvent =
                restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);
        orderApprovalEvent.fire();
    }
}
