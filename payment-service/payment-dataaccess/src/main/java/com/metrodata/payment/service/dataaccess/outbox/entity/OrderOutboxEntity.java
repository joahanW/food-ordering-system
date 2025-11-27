package com.metrodata.payment.service.dataaccess.outbox.entity;

import com.metrodata.common.domain.valueobject.PaymentStatus;
import com.metrodata.outbox.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_outbox")
@Entity
public class OrderOutboxEntity {

    @Id
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    @Enumerated(EnumType.STRING)
    private OutboxStatus outboxStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Version
    private int version;

}
