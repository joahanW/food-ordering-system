package com.metrodata.restaurant.service.messaging.publisher.kafka;

import com.metrodata.kafka.model.avro.model.RestaurantApprovalResponseAvroModel;
import com.metrodata.kafka.producer.KafkaMessageHelper;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.payment.service.domain.application.config.RestaurantServiceConfigData;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovedEvent;
import com.metrodata.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApprovedKafkaMessagePublisher implements OrderApprovedMessagePublisher {

    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfigData restaurantServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderApprovedEvent domainEvent) {
        String orderId = domainEvent.getOrderApproval().getOrderId().getValue().toString();
        log.info("Received OrderApprovedEvent for order id: {}", orderId);

        try {
            RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel = restaurantMessagingDataMapper
                    .orderApprovedEventToRestaurantApprovalResponseAvroModel(domainEvent);

            CompletableFuture<SendResult<String, RestaurantApprovalResponseAvroModel>> future =
                    kafkaProducer.send(
                            restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                            orderId,
                            restaurantApprovalResponseAvroModel
                    );

            kafkaMessageHelper.handlerSend(future,
                    restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
                    restaurantApprovalResponseAvroModel,
                    orderId,
                    "RestaurantApprovalResponseAvroModel");

            log.info("RestaurantApprovalResponseAvroModel sent to kafka at: {}", System.nanoTime());
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponseAvroModel message" +
                    " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
