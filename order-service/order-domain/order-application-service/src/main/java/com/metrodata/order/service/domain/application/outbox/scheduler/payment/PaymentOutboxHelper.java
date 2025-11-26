package com.metrodata.order.service.domain.application.outbox.scheduler.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metrodata.common.domain.valueobject.OrderStatus;
import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentEventPayload;
import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentOutboxMessage;
import com.metrodata.order.service.domain.application.ports.output.repository.PaymentOutboxRepository;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.saga.SagaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.metrodata.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxHelper {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<List<OrderPaymentOutboxMessage>>
            getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                    OutboxStatus outboxStatus, SagaStatus... sagaStatus
    ){
        return paymentOutboxRepository
                .findByTypeAndOutboxStatusAndSagaStatus(
                        ORDER_SAGA_NAME, outboxStatus, sagaStatuses
                );
    }

    @Transactional(readOnly = true)
    public Optional<OrderPaymentOutboxMessage>
            getPaymentOutboxMessageBySagaIdAndSagaStatus(
                    UUID sagaId, SagaStatus... sagaStatus
    ){
        return paymentOutboxRepository
                .findByTypeAndSagaIdAndSagaStatus(
                  ORDER_SAGA_NAME, sagaId, sagaStatus
                );
    }

    @Transactional
    public void save(OrderPaymentOutboxMessage orderPaymentOutboxMessage){
        OrderPaymentOutboxMessage response =
                paymentOutboxRepository.save(orderPaymentOutboxMessage);
        if (response == null){
            log.error("Could not save OrderPaymentOutboxMessage with outbox id: {}",
                    orderPaymentOutboxMessage.getId());
            throw new OrderDomainException(String.format("Could not save OrderPaymentOutboxMessage with outbox id: %s",
                    orderPaymentOutboxMessage.getId()));
        }
        log.info("OrderPaymentOutboxMessage saved successfully with outbox id: {}",
                orderPaymentOutboxMessage.getId());
    }

    @Transactional
    public void deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                      SagaStatus... sagaStatus){
        paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
                ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional
    public void savePaymentOutboxMessage(OrderPaymentEventPayload orderPaymentEventPayload,
                                         OrderStatus orderStatus,
                                         SagaStatus sagaStatus,
                                         OutboxStatus outboxStatus,
                                         UUID sagaId) {
        save(OrderPaymentOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderPaymentEventPayload.getCreatedAt())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderPaymentEventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    private String createPayload(OrderPaymentEventPayload orderPaymentEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderPaymentEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create OrderPaymentEventPayload object for order id: {}",
                    orderPaymentEventPayload.getOrderId(),e);
            throw new OrderDomainException(String.format("Could not create OrderPaymentEventPayload object for order id: %s",
                    orderPaymentEventPayload.getOrderId()));
        }

    }
}
