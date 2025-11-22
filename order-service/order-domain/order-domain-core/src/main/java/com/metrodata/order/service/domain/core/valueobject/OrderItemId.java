package com.metrodata.order.service.domain.core.valueobject;

import com.metrodata.common.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}
