package com.metrodata.restaurant.service.messaging.mapper;

import com.metrodata.common.domain.valueobject.ProductId;
import com.metrodata.common.domain.valueobject.RestaurantOrderStatus;
import com.metrodata.kafka.model.avro.model.OrderApprovalStatus;
import com.metrodata.kafka.model.avro.model.RestaurantApprovalRequestAvroModel;
import com.metrodata.kafka.model.avro.model.RestaurantApprovalResponseAvroModel;
import com.metrodata.restaurant.service.domain.application.dto.RestaurantApprovalRequest;
import com.metrodata.restaurant.service.domain.application.outbox.model.OrderEventPayload;
import com.metrodata.restaurant.service.domain.core.entity.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantMessagingDataMapper {

    public RestaurantApprovalRequest
    restaurantApprovalRequestAvroModelToRestaurantApprovalRequest(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel){
        return RestaurantApprovalRequest.builder()
                .id(restaurantApprovalRequestAvroModel.getId().toString())
                .sagaId(restaurantApprovalRequestAvroModel.getSagaId().toString())
                .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId().toString())
                .orderId(restaurantApprovalRequestAvroModel.getOrderId().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.valueOf(
                        restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name()))
                .products(restaurantApprovalRequestAvroModel.getProducts()
                        .stream().map(avroModel -> Product.builder()
                                .id(new ProductId(UUID.fromString(avroModel.getId())))
                                .quantity(avroModel.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .price(restaurantApprovalRequestAvroModel.getPrice())
                .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
                .build();
    }

    public RestaurantApprovalResponseAvroModel
    orderEventPayloadToRestaurantApprovalResponseAvroModel(UUID sagaId, OrderEventPayload orderEventPayload) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(sagaId)
                .setOrderId(UUID.fromString(orderEventPayload.getOrderId()))
                .setRestaurantId(UUID.fromString(orderEventPayload.getRestaurantId()))
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderEventPayload.getOrderApprovalStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }

}
