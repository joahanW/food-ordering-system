package com.metrodata.restaurant.service.domain.application.exception;

import com.metrodata.common.domain.exception.DomainException;

public class RestaurantApplicationServiceException extends DomainException {

    public RestaurantApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantApplicationServiceException(String message) {
        super(message);
    }
}
