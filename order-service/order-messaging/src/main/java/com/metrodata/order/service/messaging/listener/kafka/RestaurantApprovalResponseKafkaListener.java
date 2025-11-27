package com.metrodata.order.service.messaging.listener.kafka;

import com.metrodata.kafka.consumer.KafkaConsumer;
import com.metrodata.kafka.model.avro.model.OrderApprovalStatus;
import com.metrodata.kafka.model.avro.model.RestaurantApprovalResponseAvroModel;
import com.metrodata.order.service.domain.core.exception.OrderNotFoundException;
import com.metrodata.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.metrodata.order.service.domain.application.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
                    topics = "${order-service.restaurant-approval-response-topic-name}")
    @Override
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of restaurant approval response received with keys: {}," +
                "partitions {} and offsets {}", messages.size(), keys, partitions, offsets);

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            try {
                if (OrderApprovalStatus.APPROVED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()){
                    log.info("Processing successful restaurant approval for order id: {}",
                            restaurantApprovalResponseAvroModel.getOrderId());
                    restaurantApprovalResponseMessageListener.orderApproved(
                            orderMessagingDataMapper.approvalResponseAvroModelToRestaurantApprovalResponse(
                                    restaurantApprovalResponseAvroModel)
                    );
                }else if (OrderApprovalStatus.REJECTED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()){
                    log.info("Processing rejected order for order id: {}, with failure message: {}",
                            restaurantApprovalResponseAvroModel.getOrderId(),
                            String.join(",", restaurantApprovalResponseAvroModel.getFailureMessages()));
                    restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper
                            .approvalResponseAvroModelToRestaurantApprovalResponse(restaurantApprovalResponseAvroModel));
                }
            } catch (OptimisticLockingFailureException e) {
                log.error("Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(), e);
            }catch (OrderNotFoundException e){
                log.error("Caught order not found exception for order id: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(), e);
            }
        });
    }
}
