package com.metrodata.restaurant.service.domain.core.entity;

import com.metrodata.common.domain.entity.BaseEntity;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.common.domain.valueobject.OrderStatus;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class OrderDetail extends BaseEntity<OrderId> {
    private OrderStatus orderStatus;
    private Money totalAmount;
    private final List<Product> products;
}
