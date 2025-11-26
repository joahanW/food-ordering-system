package com.metrodata.order.service.domain.application.outbox.model.approval;

import com.metrodata.common.domain.valueobject.OrderStatus;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderApprovalOutboxMessage {

    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;

    @Setter
    private ZonedDateTime processedAt;
    private String type;

    // This add JSON property to the payload
    private String payload;

    @Setter
    private OutboxStatus outboxStatus;

    @Setter
    private SagaStatus sagaStatus;

    @Setter
    private OrderStatus orderStatus;
    private Integer version;

}