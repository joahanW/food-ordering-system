package com.metrodata.order.service.dataaccess.order.mapper;

import com.metrodata.common.domain.valueobject.*;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.entity.OrderItem;
import com.metrodata.order.service.domain.core.entity.Product;
import com.metrodata.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.metrodata.order.service.dataaccess.order.entity.OrderEntity;
import com.metrodata.order.service.dataaccess.order.entity.OrderItemEntity;
import com.metrodata.order.service.domain.core.valueobject.OrderItemId;
import com.metrodata.order.service.domain.core.valueobject.StreetAddress;
import com.metrodata.order.service.domain.core.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order){
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .price(order.getPrice().getAmount())
                .orderStatus(order.getOrderStatus())
                .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
                .items(orderItemsToOrderItemEntities(order.getItems()))
                .failureMessages(order.getFailureMessage() != null ?
                        String.join(",", order.getFailureMessage()) : "")
                .build();
        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity){
        return Order.Builder.newBuilder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(orderItemsEntitiesToOrderItems(orderEntity.getItems()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessage(orderEntity.getFailureMessages().isEmpty() ?
                    new ArrayList<>() : new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages()
                        .split(","))))
                .build();
    }


    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> orderItems){
        return orderItems.stream()
                .map(item -> OrderItemEntity.builder()
                        .id(item.getId().getValue())
                        .productId(item.getProduct().getId().getValue())
                        .price(item.getPrice().getAmount())
                        .quantity(item.getQuantity())
                        .subTotal(item.getSubTotal().getAmount())
                        .build())
                .toList();
    }

    private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress){
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .city(deliveryAddress.getCity())
                .build();
    }

    private List<OrderItem> orderItemsEntitiesToOrderItems(List<OrderItemEntity> orderItemEntities){
        return orderItemEntities.stream()
                .map(orderItemEntity -> OrderItem.Builder.newBuilder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .product(new Product(new ProductId(orderItemEntity.getProductId())))
                        .price(new Money(orderItemEntity.getPrice()))
                        .quantity(orderItemEntity.getQuantity())
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .build())
                .toList();
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address){
        return new StreetAddress(
                address.getId(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }
}
