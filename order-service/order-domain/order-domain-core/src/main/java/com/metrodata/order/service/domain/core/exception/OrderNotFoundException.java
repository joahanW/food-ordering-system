package com.metrodata.order.service.domain.core.exception;

import com.metrodata.common.domain.exception.DomainException;

public class OrderNotFoundException extends DomainException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
