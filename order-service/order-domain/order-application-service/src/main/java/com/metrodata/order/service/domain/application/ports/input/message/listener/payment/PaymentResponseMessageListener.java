package com.metrodata.order.service.domain.application.ports.input.message.listener.payment;

import com.metrodata.order.service.domain.application.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentCancelled(PaymentResponse paymentResponse);
}
