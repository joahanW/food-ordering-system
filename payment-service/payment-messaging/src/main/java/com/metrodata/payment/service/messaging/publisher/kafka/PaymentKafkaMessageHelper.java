package com.metrodata.payment.service.messaging.publisher.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class PaymentKafkaMessageHelper {

    public <T> CompletableFuture<SendResult<String, T>>
    handlerSend(CompletableFuture<SendResult<String, T>> future,
                String responseTopicName,
                T requestAvroModel,
                String orderId,
                String requestAvroModelName) {
        return future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error while sending {} message: {} to topic {}",
                        requestAvroModelName,
                        requestAvroModel.toString(), responseTopicName, exception);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Receive successful response from kafka for order id: {} " +
                                "Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
            }
        });
    }

}
