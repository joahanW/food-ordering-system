package com.metrodata.payment.service.messaging.mapper;

import com.metrodata.common.domain.valueobject.PaymentOrderStatus;
import com.metrodata.kafka.model.avro.model.PaymentRequestAvroModel;
import com.metrodata.kafka.model.avro.model.PaymentResponseAvroModel;
import com.metrodata.kafka.model.avro.model.PaymentStatus;
import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.application.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel){
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId().toString())
                .sagaId(paymentRequestAvroModel.getSagaId().toString())
                .customerId(paymentRequestAvroModel.getCustomerId().toString())
                .orderId(paymentRequestAvroModel.getOrderId().toString())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(UUID sagaId,
                                                                                OrderEventPayload orderEventPayload) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(sagaId)
                .setPaymentId(UUID.fromString(orderEventPayload.getPaymentId()))
                .setCustomerId(UUID.fromString(orderEventPayload.getCustomerId()))
                .setOrderId(UUID.fromString(orderEventPayload.getOrderId()))
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())//??
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }

}
