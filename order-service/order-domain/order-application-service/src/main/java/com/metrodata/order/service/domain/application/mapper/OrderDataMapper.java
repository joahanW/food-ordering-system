package com.metrodata.order.service.domain.application.mapper;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.ProductId;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderCommand;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderResponse;
import com.metrodata.order.service.domain.application.dto.create.OrderAddress;
import com.metrodata.order.service.domain.application.dto.track.TrackOrderResponse;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.entity.OrderItem;
import com.metrodata.order.service.domain.core.entity.Product;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import com.metrodata.order.service.domain.core.valueobject.StreetAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

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
