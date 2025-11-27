package com.metrodata.order.service.domain.application;

import com.metrodata.order.service.domain.core.OrderDomainService;
import com.metrodata.order.service.domain.application.dto.create.CreateOrderCommand;
import com.metrodata.order.service.domain.core.entity.Customer;
import com.metrodata.order.service.domain.core.entity.Order;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import com.metrodata.order.service.domain.core.event.OrderCreatedEvent;
import com.metrodata.order.service.domain.core.exception.OrderDomainException;
import com.metrodata.order.service.domain.application.mapper.OrderDataMapper;
import com.metrodata.order.service.domain.application.ports.output.repository.CustomerRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.OrderRepository;
import com.metrodata.order.service.domain.application.ports.output.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;


    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService
                .validateAndInitiateOrder(order, restaurant);
        saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if(customer.isEmpty()) {
            log.warn("Customer with id {} not found", customerId);
            throw new OrderDomainException(String.format("Customer with id %s not found", customerId));
        }
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand){
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find restaurant with id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException(String.format("Could not find restaurant with id %s", createOrderCommand.getRestaurantId()));
        }
        return optionalRestaurant.get();
    }

    private Order saveOrder(Order order){
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order {}", order);
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Saved order: {}", orderResult.getId().getValue());
        return orderResult;
    }

}
