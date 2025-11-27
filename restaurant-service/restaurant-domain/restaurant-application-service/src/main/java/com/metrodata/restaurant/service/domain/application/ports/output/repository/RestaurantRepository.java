package com.metrodata.restaurant.service.domain.application.ports.output.repository;

import com.metrodata.restaurant.service.domain.core.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
