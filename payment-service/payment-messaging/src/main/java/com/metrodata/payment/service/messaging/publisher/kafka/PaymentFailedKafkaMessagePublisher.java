package com.metrodata.payment.service.messaging.publisher.kafka;

import com.metrodata.kafka.model.avro.model.PaymentResponseAvroModel;
import com.metrodata.kafka.producer.service.KafkaProducer;
import com.metrodata.payment.service.domain.application.config.PaymentServiceConfigData;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.metrodata.payment.service.domain.core.event.PaymentFailedEvent;
import com.metrodata.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailedKafkaMessagePublisher implements PaymentFailedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final PaymentKafkaMessageHelper paymentKafkaMessageHelper;

    @Override
    public void publish(PaymentFailedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();

        log.info("Received PaymentFailedEvent for order id: {}", orderId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel =
                    paymentMessagingDataMapper.paymentFailedEventToPaymentResponseAvroModel(domainEvent);

            CompletableFuture<SendResult<String, PaymentResponseAvroModel>> future = kafkaProducer.send(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponseAvroModel);

            paymentKafkaMessageHelper.handlerSend(
                    future,
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    paymentResponseAvroModel,
                    orderId,
                    "PaymentResponseAvroModel");
            
            log.info("PaymentResponseAvroModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message to kafka " +
                    "to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }

    }
}
