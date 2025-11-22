package com.metrodata.payment.service.domain.application.ports.input.message.listener;

import com.metrodata.payment.service.domain.application.dto.RestaurantApprovalRequest;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;

public interface RestaurantApprovalRequestMessageListener  {
    void approvedOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
