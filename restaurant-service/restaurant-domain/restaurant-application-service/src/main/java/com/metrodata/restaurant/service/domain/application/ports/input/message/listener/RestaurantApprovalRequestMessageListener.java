package com.metrodata.restaurant.service.domain.application.ports.input.message.listener;

import com.metrodata.restaurant.service.domain.application.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener  {
    void approvedOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
