package com.metrodata.order.service.domain.application;

import com.metrodata.common.domain.valueobject.OrderStatus;
import com.metrodata.common.domain.valueobject.PaymentStatus;
import com.metrodata.order.service.domain.application.dto.message.PaymentResponse;
import com.metrodata.order.service.domain.application.mapper.OrderDataMapper;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalOutboxMessage;
import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentOutboxMessage;
import com.metrodata.order.service.domain.application.outbox.scheduler.approval.RestaurantApprovalOutboxHelper;
import com.metrodata.order.service.domain.application.outbox.scheduler.payment.PaymentOutboxHelper;
import com.metrodata.order.service.domain.core.OrderDomainService;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.event.OrderPaidEvent;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.saga.SagaStatus;
import com.metrodata.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.metrodata.common.domain.DomainConstant.UTC;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private RestaurantApprovalOutboxHelper restaurantApprovalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                    UUID.fromString(paymentResponse.getSagaId()),
                    SagaStatus.STARTED
        );

        // This for validation if saga send multiple time in instance consumer
        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        OrderPaidEvent domainEvent = completePaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                domainEvent.getOrder().getOrderStatus(), sagaStatus));

        restaurantApprovalOutboxHelper.saveApprovalOutboxMessage(
                orderDataMapper.orderPaidEventToOrderApprovalEventPayload(domainEvent),
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(paymentResponse.getSagaId())
        );

        log.info("Order with id: {} is paid", domainEvent.getOrder().getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
            paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(paymentResponse.getSagaId()),
                getCurrentSagaStatus(paymentResponse.getPaymentStatus())
        );

        if (orderPaymentOutboxMessageResponse.isEmpty()){
            log.info("An outbox message with saga id: {} is already roll backed!", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        Order order = rollbackPaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());
        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                order.getOrderStatus(), sagaStatus));

        if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED){
            restaurantApprovalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                    paymentResponse.getSagaId(),
                    order.getOrderStatus(),
                    sagaStatus
            ));
        }

        log.info("Order with id: {} is cancelled", order.getId());
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                                                                     OrderStatus orderStatus,
                                                                     SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse){
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        return domainEvent;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus){
            case COMPLETED -> new SagaStatus[] {SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[] {SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[] {SagaStatus.STARTED, SagaStatus.PROCESSING};
        };
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(String sagaId,
                                                                       OrderStatus orderStatus,
                                                                       SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
            restaurantApprovalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId),
                SagaStatus.COMPENSATING
        );

        if (orderApprovalOutboxMessageResponse.isEmpty()){
            throw new OrderDomainException("Approval outbox message could not be found in " +
                    SagaStatus.COMPENSATING.name() + " status!");
        }
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

}
