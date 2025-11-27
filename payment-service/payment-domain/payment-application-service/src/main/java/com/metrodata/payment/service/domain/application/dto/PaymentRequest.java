package com.metrodata.payment.service.domain.application.dto;

import com.metrodata.common.domain.valueobject.PaymentOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private String id;
    private String sagaId;
    private String orderId;
    private String customerId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentOrderStatus paymentOrderStatus;

}
