package com.metrodata.payment.service.domain.application;

import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.application.ports.input.message.listener.PaymentRequestMessageListener;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.metrodata.payment.service.domain.core.event.PaymentCancelledEvent;
import com.metrodata.payment.service.domain.core.event.PaymentCompletedEvent;
import com.metrodata.payment.service.domain.core.event.PaymentEvent;
import com.metrodata.payment.service.domain.core.event.PaymentFailedEvent;
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
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with payment id: {} and order id: {}",
                paymentEvent.getPayment().getId().getValue(),
                paymentEvent.getPayment().getOrderId().getValue());
        paymentEvent.fire();
    }
}
