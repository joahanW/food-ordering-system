package com.metrodata.order.service.domain.application;

import com.metrodata.common.domain.event.EmptyEvent;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.order.service.domain.application.dto.message.PaymentResponse;
import com.metrodata.order.service.domain.application.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.core.OrderDomainService;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.event.OrderPaidEvent;
import com.metrodata.order.service.domain.core.exception.OrderNotFoundException;
import com.metrodata.saga.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent domainEvent =
                orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);
       orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is paid", order.getId());
        return domainEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is cancelled", order.getId());
        return EmptyEvent.INSTANCE;
    }

}
