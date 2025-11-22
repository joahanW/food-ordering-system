package com.metrodata.restaurant.service.domain.core.exception;

import com.metrodata.common.domain.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }
}
