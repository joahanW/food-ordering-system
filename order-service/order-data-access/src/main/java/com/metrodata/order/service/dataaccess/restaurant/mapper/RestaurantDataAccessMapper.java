package com.metrodata.order.service.dataaccess.restaurant.mapper;

import com.metrodata.common.dataaccess.restaurant.entity.RestaurantEntity;
import com.metrodata.common.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.ProductId;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.order.service.domain.core.entity.Product;
import com.metrodata.order.service.domain.core.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities){
        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("Restaurant could not be found!!"));
        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                    new Product(new ProductId(entity.getProductId()),
                                entity.getProductName(),
                        new Money(entity.getProductPrice())))
                .toList();
        return Restaurant.Builder.newBuilder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(restaurantProducts)
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }

}
