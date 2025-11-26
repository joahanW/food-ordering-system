package com.metrodata.order.service.domain.application.mapper;

import com.metrodata.common.domain.valueobject.*;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderCommand;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderResponse;
import com.metrodata.order.service.domain.application.dto.create.OrderAddress;
import com.metrodata.order.service.domain.application.dto.track.TrackOrderResponse;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalEventPayload;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalEventProduct;
import com.metrodata.order.service.domain.application.outbox.model.payment.OrderPaymentEventPayload;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.entity.OrderItem;
import com.metrodata.order.service.domain.core.entity.Product;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import com.metrodata.order.service.domain.core.event.OrderCancelledEvent;
import com.metrodata.order.service.domain.core.event.OrderCreatedEvent;
import com.metrodata.order.service.domain.core.event.OrderPaidEvent;
import com.metrodata.order.service.domain.core.valueobject.StreetAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand){
        return Restaurant.Builder.newBuilder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream()
                        .map(orderItem ->
                            new Product(new ProductId(orderItem.getProductId()))
                        ).toList())
                .build();

    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand){
        return Order.Builder.newBuilder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message){
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order){
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessage())
                .build();
    }

    public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(
            OrderCreatedEvent orderCreatedEvent){
        return OrderPaymentEventPayload.builder()
                .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
                .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
                .price(orderCreatedEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCreatedEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

    public OrderApprovalEventPayload orderPaidEventToOrderApprovalEventPayload(OrderPaidEvent orderPaidEvent){
        return OrderApprovalEventPayload.builder()
                .orderId(orderPaidEvent.getOrder().getId().getValue().toString())
                .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getValue().toString())
                .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
                .products(orderPaidEvent.getOrder().getItems().stream().map(orderItem ->
                        OrderApprovalEventProduct.builder()
                                .id(orderItem.getProduct().getId().getValue().toString())
                                .quantity(orderItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .price(orderPaidEvent.getOrder().getPrice().getAmount())
                .createdAt(orderPaidEvent.getCreatedAt())
                .build();
    }

    public OrderPaymentEventPayload orderCancelledEventToOrderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent) {
        return OrderPaymentEventPayload.builder()
                .customerId(orderCancelledEvent.getOrder().getCustomerId().getValue().toString())
                .orderId(orderCancelledEvent.getOrder().getId().getValue().toString())
                .price(orderCancelledEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCancelledEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
                .build();
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress address){
        return new StreetAddress(
                UUID.randomUUID(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
                );
    }


    private List<OrderItem> orderItemToOrderItemEntities(List<com.metrodata.order.service.domain.application.dto.create.OrderItem> orderItems){
        return orderItems.stream()
                .map(orderItem ->
                    OrderItem.Builder.newBuilder()
                            .product(new Product((new ProductId(orderItem.getProductId()))))
                            .price(new Money(orderItem.getPrice()))
                            .quantity(orderItem.getQuantity())
                            .subTotal(new Money(orderItem.getSubTotal()))
                            .build()
                ).toList();
    }
}
