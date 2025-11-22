package com.metrodata.payment.service.domain.application.ports.input.message.listener;

import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.core.entity.Payment;

public interface PaymentRequestMessageListener {
    void completePayment(PaymentRequest paymentRequest);
    void cancelPayment(PaymentRequest paymentRequest);
}
