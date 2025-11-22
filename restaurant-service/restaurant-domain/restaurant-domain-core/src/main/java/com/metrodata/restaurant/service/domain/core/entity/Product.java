package com.metrodata.restaurant.service.domain.core.entity;

import com.metrodata.common.domain.entity.BaseEntity;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.ProductId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private final int quantity;
    private boolean available;

    public void updateWithConfirmedNamePriceAndAvailability(String name, Money price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }
}
