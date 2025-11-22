package com.metrodata.payment.service.domain.application;

import com.metrodata.payment.service.domain.application.dto.RestaurantApprovalRequest;
import com.metrodata.payment.service.domain.application.mapper.RestaurantDataMapper;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.OrderRejectedMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.repository.OrderApprovalRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.RestaurantRepository;
import com.metrodata.restaurant.service.domain.core.RestaurantDomainService;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;
import com.metrodata.restaurant.service.domain.core.event.OrderApprovalEvent;
import com.metrodata.restaurant.service.domain.core.exception.RestaurantNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest){
        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(
                restaurant, failureMessages,
                orderApprovedMessagePublisher,
                orderRejectedMessagePublisher);
        orderApprovalRepository.save(restaurant.getOrderApproval());
        return orderApprovalEvent;
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper
                .restaurantApprovalRequesToRestaurant(restaurantApprovalRequest);
        Restaurant restaurantEntity = restaurantRepository.findRestaurantInformation(restaurant)
                .orElseThrow(() -> {
                    log.error("Restaurant with id : {} not found", restaurant.getId().getValue());
                    return new RestaurantNotFoundException(String.format("Restaurant with id : %s not found",
                            restaurant.getId().getValue()));
                });

        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> {
            restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                if (p.getId().equals(product.getId())) {
                    product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                }
            });
        });
        return restaurant;
    }

}
