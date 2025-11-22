package com.metrodata.order.service.dataaccess.restaurant.adapter;

import com.metrodata.common.dataaccess.restaurant.entity.RestaurantEntity;
import com.metrodata.common.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import com.metrodata.order.service.domain.application.ports.output.repository.RestaurantRepository;
import com.metrodata.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProduct =
                restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);
        Optional<List<RestaurantEntity>> restaurantEntities =
                restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
                        restaurant.getId().getValue(), restaurantProduct);
        return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
