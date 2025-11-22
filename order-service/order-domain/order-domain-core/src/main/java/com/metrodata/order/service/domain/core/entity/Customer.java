package com.metrodata.order.service.domain.core.entity;

import com.metrodata.common.domain.entity.AggregateRoot;
import com.metrodata.common.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    public Customer() {
    }

    public Customer(CustomerId customerId) {
        setId(customerId);
    }
}
