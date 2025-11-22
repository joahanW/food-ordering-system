package com.metrodata.order.service.domain.application.ports.output.repository;

import com.metrodata.order.service.domain.core.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
