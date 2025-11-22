package com.metrodata.payment.service.domain.application.dto;

import com.metrodata.common.domain.valueobject.RestaurantOrderStatus;
import com.metrodata.restaurant.service.domain.core.entity.Product;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RestaurantApprovalRequest {

    private String id;
    private String sagaId;
    private String restaurantId;
    private String orderId;
    private RestaurantOrderStatus restaurantOrderStatus;
    private List<Product> products;
    private BigDecimal price;
    private Instant createdAt;
}
