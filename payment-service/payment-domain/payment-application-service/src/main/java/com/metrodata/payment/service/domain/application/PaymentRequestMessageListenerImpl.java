package com.metrodata.payment.service.domain.application;

import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.application.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper paymentRequestHelper;

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        paymentRequestHelper.persistPayment(paymentRequest);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
       paymentRequestHelper.persistCancelPayment(paymentRequest);
    }

}
