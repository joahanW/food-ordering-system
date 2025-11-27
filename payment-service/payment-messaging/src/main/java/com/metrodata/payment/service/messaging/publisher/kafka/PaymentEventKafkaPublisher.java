package com.metrodata.payment.service.messaging.publisher.kafka;

import com.metrodata.kafka.model.avro.model.PaymentResponseAvroModel;
import com.metrodata.kafka.producer.KafkaMessageHelper;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.payment.service.domain.application.config.PaymentServiceConfigData;
import com.metrodata.payment.service.domain.application.outbox.model.OrderEventPayload;
import com.metrodata.payment.service.domain.application.outbox.model.OrderOutboxMessage;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.metrodata.payment.service.messaging.mapper.PaymentMessagingDataMapper;
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
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.convertPayload(orderOutboxMessage.getPayload(), OrderEventPayload.class);

        UUID sagaId = orderOutboxMessage.getSagaId();

        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper
                    .orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);

            CompletableFuture<SendResult<String, PaymentResponseAvroModel>> future =
                    kafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    sagaId.toString(),
                    paymentResponseAvroModel);

            kafkaMessageHelper.handlerSend(
                    future,
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    paymentResponseAvroModel,
                    paymentResponseAvroModel.getOrderId().toString(),
                    "PaymentResponseAvroModel",
                    orderOutboxMessage,
                    outboxCallback
            );

            log.info("PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    paymentResponseAvroModel.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message" +
                            " to kafka with order id: {} and saga id: {}, error: {}",
                    orderEventPayload.getOrderId(), sagaId, e.getMessage());
        }
    }
}
