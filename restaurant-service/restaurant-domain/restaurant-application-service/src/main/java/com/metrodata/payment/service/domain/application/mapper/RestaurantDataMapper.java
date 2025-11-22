package com.metrodata.payment.service.domain.application.mapper;

import com.metrodata.common.domain.valueobject.*;
import com.metrodata.payment.service.domain.application.dto.RestaurantApprovalRequest;
import com.metrodata.restaurant.service.domain.core.entity.OrderDetail;
import com.metrodata.restaurant.service.domain.core.entity.Product;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {

    public Restaurant restaurantApprovalRequesToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest){
        return Restaurant.builder()
                .id(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getId())))
                .orderDetail(OrderDetail.builder()
                        .id(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                        .products(restaurantApprovalRequest.getProducts().stream().map(
                                product -> Product.builder()
                                        .id(product.getId())
                                        .name(product.getName())
                                        .price(product.getPrice())
                                        .quantity(product.getQuantity())
                                        .build())
                                .collect(Collectors.toList())
                        )
                        .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }

}
