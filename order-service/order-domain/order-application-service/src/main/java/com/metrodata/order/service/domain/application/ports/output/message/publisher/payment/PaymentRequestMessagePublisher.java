package com.metrodata.order.service.domain.application.ports.output.message.publisher.payment;

import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentOutboxMessage;
import com.metrodata.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface PaymentRequestMessagePublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);

}
