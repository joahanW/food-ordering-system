package com.metrodata.restaurant.service.messaging.publisher.kafka;

import com.metrodata.kafka.model.avro.model.RestaurantApprovalResponseAvroModel;
import com.metrodata.kafka.producer.KafkaMessageHelper;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.restaurant.service.domain.application.config.RestaurantServiceConfigData;
import com.metrodata.restaurant.service.domain.application.outbox.model.OrderEventPayload;
import com.metrodata.restaurant.service.domain.application.outbox.model.OrderOutboxMessage;
import com.metrodata.restaurant.service.domain.application.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import com.metrodata.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
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
public class RestaurantApprovalEventKafkaPublisher implements RestaurantApprovalResponseMessagePublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfigData restaurantServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.convertPayload(orderOutboxMessage.getPayload(),
                        OrderEventPayload.class);

        UUID sagaId = orderOutboxMessage.getSagaId();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);
        try {
            RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
                    restaurantMessagingDataMapper
                            .orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, orderEventPayload);

            CompletableFuture<SendResult<String, RestaurantApprovalResponseAvroModel>> future =
                    kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                            sagaId.toString(),
                            restaurantApprovalResponseAvroModel);

            kafkaMessageHelper.handlerSend(
                    future,
                    restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                    restaurantApprovalResponseAvroModel,
                    restaurantApprovalResponseAvroModel.getOrderId().toString(),
                    "RestaurantApprovalResponseAvroModel",
                    orderOutboxMessage,
                    outboxCallback
            );

            log.info("RestaurantApprovalResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    restaurantApprovalResponseAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponseAvroModel message" +
                            " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }

}
