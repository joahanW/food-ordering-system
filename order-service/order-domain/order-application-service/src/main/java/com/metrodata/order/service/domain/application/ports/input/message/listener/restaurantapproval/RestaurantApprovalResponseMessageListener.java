package com.metrodata.order.service.domain.application.ports.input.message.listener.restaurantapproval;

import com.metrodata.order.service.domain.application.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalResponseMessageListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);

}
