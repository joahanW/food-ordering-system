package com.metrodata.payment.service.domain.application.mapper;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.core.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestToPayment(PaymentRequest paymentRequest) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }
}
