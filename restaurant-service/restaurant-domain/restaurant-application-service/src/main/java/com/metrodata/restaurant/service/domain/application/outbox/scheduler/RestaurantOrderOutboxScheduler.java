package com.metrodata.restaurant.service.domain.application.outbox.scheduler;

import com.metrodata.outbox.OutboxScheduler;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.restaurant.service.domain.application.outbox.model.OrderOutboxMessage;
import com.metrodata.restaurant.service.domain.application.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantOrderOutboxScheduler implements OutboxScheduler {

    private final RestaurantOrderOutboxHelper orderOutboxHelper;
    private final RestaurantApprovalResponseMessagePublisher responseMessagePublisher;

    @Transactional
    @Scheduled(fixedRateString = "${restaurant-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${restaurant-service.outbox-scheduler-initial-delay}")
    @Override
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> outboxMessagesResponse =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if (outboxMessagesResponse.isPresent() && outboxMessagesResponse.get().size() > 0) {
            List<OrderOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} OrderOutboxMessage with ids {}, sending to message bus!", outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage ->
                            outboxMessage.getId().toString()).collect(Collectors.joining(",")));

            outboxMessages.forEach(orderOutboxMessage ->
                    responseMessagePublisher.publish(orderOutboxMessage,
                            orderOutboxHelper::updateOutboxStatus));

            log.info("{} OrderOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }

}