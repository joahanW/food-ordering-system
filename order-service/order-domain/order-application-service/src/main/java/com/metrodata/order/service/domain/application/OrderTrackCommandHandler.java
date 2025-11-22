package com.metrodata.order.service.domain.application;

import com.metrodata.order.service.domain.application.dto.track.TrackOrderQuery;
import com.metrodata.order.service.domain.application.dto.track.TrackOrderResponse;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.exception.OrderNotFoundException;
import com.metrodata.order.service.domain.application.mapper.OrderDataMapper;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.core.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> orderResult = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
        if (orderResult.isEmpty()) {
            log.warn("Could not find order woth tracking id : {}", trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException(String.format(
                    "Could not find order with tracking id  ", trackOrderQuery.getOrderTrackingId()
            ));
        }
        return orderDataMapper.orderToTrackOrderResponse(orderResult.get());
    }

}
