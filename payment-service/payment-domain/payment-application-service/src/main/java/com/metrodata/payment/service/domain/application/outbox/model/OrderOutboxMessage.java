package com.metrodata.payment.service.domain.application.outbox.model;

import com.metrodata.common.domain.valueobject.PaymentStatus;
import com.metrodata.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
    private PaymentStatus paymentStatus;
    private OutboxStatus outboxStatus;
    private int version;
}
