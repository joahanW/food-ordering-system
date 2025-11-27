package com.metrodata.payment.service.domain.application.ports.output.message.publisher;

import com.metrodata.outbox.OutboxStatus;
import com.metrodata.payment.service.domain.application.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponseMessagePublisher {
    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
