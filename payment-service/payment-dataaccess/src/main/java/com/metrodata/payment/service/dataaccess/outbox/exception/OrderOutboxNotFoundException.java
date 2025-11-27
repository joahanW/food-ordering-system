package com.metrodata.payment.service.dataaccess.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException{
    public OrderOutboxNotFoundException(String message) {
        super(message);
    }
}
