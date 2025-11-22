package com.metrodata.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaMessageHelper {

    public <T> CompletableFuture<SendResult<String, T>>
    handlerSend(CompletableFuture<SendResult<String, T>> future,
                String topicName,
                T avroData,
                String orderId,
                String avroModelName) {
        return future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error while sending {} message: {} to topic {}",
                        avroModelName,
                        avroData.toString(), topicName, exception);
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
