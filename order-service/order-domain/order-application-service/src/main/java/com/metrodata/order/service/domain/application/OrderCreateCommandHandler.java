package com.metrodata.order.service.domain.application;

import com.metrodata.order.service.domain.application.outbox.scheduler.payment.PaymentOutboxHelper;
import com.metrodata.order.service.domain.application.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.metrodata.order.service.domain.core.OrderDomainService;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderCommand;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderResponse;
import com.metrodata.order.service.domain.core.entity.Customer;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import com.metrodata.order.service.domain.core.event.OrderCreatedEvent;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
import com.metrodata.order.service.domain.application.mapper.OrderDataMapper;
import com.metrodata.order.service.domain.application.ports.output.repository.CustomerRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.RestaurantRepository;
import com.metrodata.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        CreateOrderResponse createOrderResponse =
                orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(),
                "Order Created Successfully");

        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID());

        log.info("Returning CreateOrderResponse with order id: {}", orderCreatedEvent.getOrder().getId());
        return createOrderResponse;
    }

}
