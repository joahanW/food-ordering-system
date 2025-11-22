package com.metrodata.payment.service.messaging.mapper;

import com.metrodata.common.domain.valueobject.PaymentOrderStatus;
import com.metrodata.kafka.model.avro.model.PaymentRequestAvroModel;
import com.metrodata.kafka.model.avro.model.PaymentResponseAvroModel;
import com.metrodata.kafka.model.avro.model.PaymentStatus;
import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.core.entity.Payment;
import com.metrodata.payment.service.domain.core.event.PaymentCancelledEvent;
import com.metrodata.payment.service.domain.core.event.PaymentCompletedEvent;
import com.metrodata.payment.service.domain.core.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel
    paymentCompletedEventToPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent) {
        Payment payment = paymentCompletedEvent.getPayment();
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(null)
                .setPaymentId(payment.getId().getValue())
                .setCustomerId(payment.getCustomerId().getValue())
                .setOrderId(payment.getOrderId().getValue())
                .setPrice(payment.getPrice().getAmount())
                .setCreatedAt(payment.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
                .setFailureMessages(paymentCompletedEvent.getFailureMessage())
                .build();
    }

    public PaymentResponseAvroModel
    paymentCancelledEventToPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent){
        Payment payment = paymentCancelledEvent.getPayment();
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(null)
                .setPaymentId(payment.getId().getValue())
                .setCustomerId(payment.getCustomerId().getValue())
                .setOrderId(payment.getOrderId().getValue())
                .setPrice(payment.getPrice().getAmount())
                .setCreatedAt(payment.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
                .setFailureMessages(paymentCancelledEvent.getFailureMessage())
                .build();
    }

    public PaymentResponseAvroModel
    paymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent){
        Payment payment = paymentFailedEvent.getPayment();
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(null)
                .setPaymentId(payment.getId().getValue())
                .setCustomerId(payment.getCustomerId().getValue())
                .setOrderId(payment.getOrderId().getValue())
                .setPrice(payment.getPrice().getAmount())
                .setCreatedAt(payment.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
                .setFailureMessages(paymentFailedEvent.getFailureMessage())
                .build();
    }

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

}
