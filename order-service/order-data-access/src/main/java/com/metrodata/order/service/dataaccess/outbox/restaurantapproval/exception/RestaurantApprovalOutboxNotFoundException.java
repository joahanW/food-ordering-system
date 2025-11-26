package com.metrodata.order.service.dataaccess.outbox.restaurantapproval.exception;

public class RestaurantApprovalOutboxNotFoundException extends RuntimeException{
    public RestaurantApprovalOutboxNotFoundException(String message) {
        super(message);
    }
}
