package com.metrodata.order.service.dataaccess.outbox.restaurantapproval.mapper;

import com.metrodata.order.service.dataaccess.outbox.restaurantapproval.entity.RestaurantApprovalOutboxEntity;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class RestaurantApprovalOutboxDataAccessMapper {
    public RestaurantApprovalOutboxEntity orderCreatedOutboxMessageToOutboxEntity(OrderApprovalOutboxMessage
                                                                                orderApprovalOutboxMessage) {
        return RestaurantApprovalOutboxEntity.builder()
                .id(orderApprovalOutboxMessage.getId())
                .sagaId(orderApprovalOutboxMessage.getSagaId())
                .createdAt(orderApprovalOutboxMessage.getCreatedAt())
                .type(orderApprovalOutboxMessage.getType())
                .payload(orderApprovalOutboxMessage.getPayload())
                .orderStatus(orderApprovalOutboxMessage.getOrderStatus())
                .sagaStatus(orderApprovalOutboxMessage.getSagaStatus())
                .outboxStatus(orderApprovalOutboxMessage.getOutboxStatus())
                .version(orderApprovalOutboxMessage.getVersion())
                .build();
    }

    public OrderApprovalOutboxMessage approvalOutboxEntityToOrderApprovalOutboxMessage(RestaurantApprovalOutboxEntity
                                                                                               restaurantApprovalOutboxEntity) {
        return OrderApprovalOutboxMessage.builder()
                .id(restaurantApprovalOutboxEntity.getId())
                .sagaId(restaurantApprovalOutboxEntity.getSagaId())
                .createdAt(restaurantApprovalOutboxEntity.getCreatedAt())
                .type(restaurantApprovalOutboxEntity.getType())
                .payload(restaurantApprovalOutboxEntity.getPayload())
                .orderStatus(restaurantApprovalOutboxEntity.getOrderStatus())
                .sagaStatus(restaurantApprovalOutboxEntity.getSagaStatus())
                .outboxStatus(restaurantApprovalOutboxEntity.getOutboxStatus())
                .version(restaurantApprovalOutboxEntity.getVersion())
                .build();
    }
}
