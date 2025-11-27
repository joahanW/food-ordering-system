package com.metrodata.order.service.messaging.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrodata.kafka.model.avro.model.PaymentRequestAvroModel;
import com.metrodata.kafka.producer.KafkaMessageHelper;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.order.service.domain.application.OrderPaymentSaga;
import com.metrodata.order.service.domain.application.config.OrderServiceConfigData;
import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentEventPayload;
import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentOutboxMessage;
import com.metrodata.order.service.domain.application.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
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
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final OrderPaymentSaga orderPaymentSaga;

    @Override
    public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                        BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {
        OrderPaymentEventPayload orderPaymentEventPayload =
                kafkaMessageHelper.convertPayload(
                        orderPaymentOutboxMessage.getPayload(),
                        OrderPaymentEventPayload.class);

        UUID sagaId = orderPaymentOutboxMessage.getSagaId();

        log.info("Received OrderPaymentOutboxMessage for Order id: {} and SagaId: {}",
                orderPaymentEventPayload.getOrderId(), sagaId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper
                    .orderPaymentEventToPaymentRequestAvroModel(sagaId, orderPaymentEventPayload);

            CompletableFuture<SendResult<String, PaymentRequestAvroModel>> future =
                    kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    sagaId.toString(), paymentRequestAvroModel);

            kafkaMessageHelper.handlerSend(
                    future,
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    paymentRequestAvroModel,
                    orderPaymentEventPayload.getOrderId(),
                    "PaymentRequestAvroModel",
                    orderPaymentOutboxMessage,
                    outboxCallback
            );

            log.info("OrderPaymentEventPayload sent to kafka for order id: {} and saga id: {}",
                    orderPaymentEventPayload.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending OrderPaymentEventPayload to kafka with" +
                    "order id: {} and sagaId: {}, error: {}",
                    orderPaymentEventPayload.getOrderId(), sagaId, e.getMessage());
        }

    }

}
