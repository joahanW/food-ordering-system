package com.metrodata.restaurant.service.domain.application.ports.output.message.publisher;

import com.metrodata.outbox.OutboxStatus;
import com.metrodata.restaurant.service.domain.application.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface RestaurantApprovalResponseMessagePublisher {

    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
