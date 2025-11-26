package com.metrodata.order.service.domain.application;

import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.common.domain.valueobject.OrderStatus;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.exception.OrderNotFoundException;
import com.metrodata.saga.SagaStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public Order findOrder(String orderId) {
        return orderRepository.findById(new OrderId(UUID.fromString(orderId)))
                .orElseThrow(() -> {
                    log.error("Order with id: {} not found!", orderId);
                    return new OrderNotFoundException(String.format("Order with id: %s not found!", orderId));
                });
    }

    public void saveOrder(Order order){
        orderRepository.save(order);
    }

    public SagaStatus orderStatusToSagaStatus(OrderStatus orderStatus){
        return switch (orderStatus) {
            case PAID -> SagaStatus.PROCESSING;
            case APPROVED -> SagaStatus.SUCCEEDED;
            case CANCELLING -> SagaStatus.COMPENSATING; // Memberikan Kompensasi
            case CANCELLED -> SagaStatus.COMPENSATED; // Diberikan Kompensasi
            default -> SagaStatus.STARTED;
        };
    }

}
