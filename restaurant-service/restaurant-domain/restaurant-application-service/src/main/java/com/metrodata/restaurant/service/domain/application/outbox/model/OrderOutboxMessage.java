package com.metrodata.restaurant.service.domain.application.outbox.model;

import com.metrodata.common.domain.valueobject.OrderApprovalStatus;
import com.metrodata.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class OrderOutboxMessage {
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    private OutboxStatus outboxStatus;
    private OrderApprovalStatus approvalStatus;
    private int version;
}
