package com.metrodata.payment.service.messaging.listener.kafka;

import com.metrodata.kafka.consumer.KafkaConsumer;
import com.metrodata.kafka.model.avro.model.PaymentOrderStatus;
import com.metrodata.kafka.model.avro.model.PaymentRequestAvroModel;
import com.metrodata.kafka.model.avro.model.PaymentStatus;
import com.metrodata.payment.service.domain.application.ports.input.message.listener.PaymentRequestMessageListener;
import com.metrodata.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
                   topics = "${payment-service.payment-request-topic-name}")
    @Override
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment requests received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);
        messages.forEach(paymentRequestAvroModel -> {
            if (PaymentOrderStatus.PENDING == paymentRequestAvroModel.getPaymentOrderStatus()){
                log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());

                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                                .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            }else if(PaymentOrderStatus.CANCELLED == paymentRequestAvroModel.getPaymentOrderStatus()) {
                log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper
                        .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            }
        });
    }
}
