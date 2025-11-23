package com.metrodata.order.service.messaging.publisher.kafka;

import com.metrodata.kafka.model.avro.model.RestaurantApprovalRequestAvroModel;
import com.metrodata.order.service.domain.application.config.OrderServiceConfigData;
import com.metrodata.order.service.domain.core.event.OrderPaidEvent;
import com.metrodata.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.metrodata.order.service.domain.application.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.metrodata.kafka.producer.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                    orderMessagingDataMapper.orderPaidEventRestaurantApprovalRequestAvroModel(domainEvent);

            CompletableFuture<SendResult<String, RestaurantApprovalRequestAvroModel>> future =
                    kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                                        orderId,
                                        restaurantApprovalRequestAvroModel);

            orderKafkaMessageHelper.handlerSend(
                    future,
                    orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    restaurantApprovalRequestAvroModel,
                    orderId,
                    "RestaurantApprovalRequestAvroModel");
            log.info("RestaurantApprovalRequestAvroModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
           log.error("Error while sending RestaurantApprovalRequestAvroModel " +
                   "message to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }

}
