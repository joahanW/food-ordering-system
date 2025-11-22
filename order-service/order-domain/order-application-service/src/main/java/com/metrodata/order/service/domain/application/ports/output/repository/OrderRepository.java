package com.metrodata.order.service.domain.application.ports.output.repository;

import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findByTrackingId(TrackingId trackingId);

}
