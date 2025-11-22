package com.metrodata.order.service.messaging.publisher.kafka;

import com.metrodata.kafka.model.avro.model.PaymentRequestAvroModel;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.order.service.domain.application.config.OrderServiceConfigData;
import com.metrodata.order.service.domain.core.event.OrderCancelledEvent;
import com.metrodata.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.metrodata.order.service.domain.application.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCancelledEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                    orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(domainEvent);

            CompletableFuture<SendResult<String, PaymentRequestAvroModel>> future =
                    kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                                        orderId,
                                        paymentRequestAvroModel);
            orderKafkaMessageHelper.handlerSend(
                    future,
                    orderServiceConfigData.getPaymentResponseTopicName(),
                    paymentRequestAvroModel,
                    orderId,
                    "PaymentRequestAvroModel");

            log.info("PaymentRequestAvroModel sent to kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka " +
                    "with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
