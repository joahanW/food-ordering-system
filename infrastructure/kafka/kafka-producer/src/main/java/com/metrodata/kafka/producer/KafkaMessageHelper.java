package com.metrodata.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
import com.metrodata.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <T, U> CompletableFuture<SendResult<String, T>>
    handlerSend(CompletableFuture<SendResult<String, T>> future,
                String topicName,
                T avroData,
                String orderId,
                String avroModelName,
                U outboxMessage,
                BiConsumer<U, OutboxStatus> outboxCallback) {
        return future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error while sending {} message: {} to topic {}",
                        avroModelName,
                        avroData.toString(), topicName, exception);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Receive successful response from kafka for order id: {} " +
                                "Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
        });
    }

    public <T> T convertPayload(String payload, Class<T> outputType) {
        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!", outputType.getName(), e);
            throw new OrderDomainException(String.format("Could not read %s Object!",outputType.getName()), e);
        }
    }
}



