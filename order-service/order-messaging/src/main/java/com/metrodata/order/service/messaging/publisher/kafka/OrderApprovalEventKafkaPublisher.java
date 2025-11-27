package com.metrodata.order.service.messaging.publisher.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrodata.kafka.model.avro.model.RestaurantApprovalRequestAvroModel;
import com.metrodata.kafka.producer.KafkaMessageHelper;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.order.service.domain.application.OrderApprovalSaga;
import com.metrodata.order.service.domain.application.config.OrderServiceConfigData;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalEventPayload;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalOutboxMessage;
import com.metrodata.order.service.domain.application.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.metrodata.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.metrodata.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final OrderApprovalSaga orderApprovalSaga;

    @Override
    public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                        BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback) {
        OrderApprovalEventPayload orderApprovalEventPayload =
                kafkaMessageHelper.convertPayload(orderApprovalOutboxMessage.getPayload(), OrderApprovalEventPayload.class);

        UUID sagaId = orderApprovalOutboxMessage.getSagaId();

        log.info("Received OrderApprovalOutboxMessage for Order id: {} and SagaId: {}",
                orderApprovalEventPayload.getOrderId(), sagaId);

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                    orderMessagingDataMapper
                            .orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId,
                                    orderApprovalEventPayload);

            CompletableFuture<SendResult<String, RestaurantApprovalRequestAvroModel>> future =
                    kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    sagaId.toString(),
                    restaurantApprovalRequestAvroModel);


            kafkaMessageHelper.handlerSend(
                    future,
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    restaurantApprovalRequestAvroModel,
                    restaurantApprovalRequestAvroModel.getOrderId().toString(),
                    "RestaurantApprovalRequestAvroModel",
                    orderApprovalOutboxMessage,
                    outboxCallback
            );

            log.info("OrderApprovalEventPayload sent to kafka for order id: {} and saga id: {}",
                    restaurantApprovalRequestAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending OrderApprovalEventPayload to kafka for order id: {} and saga id: {}," +
                    " error: {}", orderApprovalEventPayload.getOrderId(), sagaId, e.getMessage());
        }


    }
}
