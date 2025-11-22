package com.metrodata.restaurant.service.dataaccess.mapper;

import com.metrodata.common.dataaccess.restaurant.entity.RestaurantEntity;
import com.metrodata.common.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.common.domain.valueobject.ProductId;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.restaurant.service.dataaccess.entity.OrderApprovalEntity;
import com.metrodata.restaurant.service.domain.core.entity.OrderApproval;
import com.metrodata.restaurant.service.domain.core.entity.OrderDetail;
import com.metrodata.restaurant.service.domain.core.entity.Product;
import com.metrodata.restaurant.service.domain.core.entity.Restaurant;
import com.metrodata.restaurant.service.domain.core.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant){
        return restaurant.getOrderDetail().getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities){
        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst().orElseThrow(() ->
                        new RestaurantDataAccessException("No restaurants found!!"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                        Product.builder()
                                .id(new ProductId(entity.getProductId()))
                                .name(entity.getProductName())
                                .price(new Money(entity.getProductPrice()))
                                .available(entity.getProductAvailable())
                                .build())
                .collect(Collectors.toList());

        return Restaurant.builder()
                .id(new RestaurantId(restaurantEntity.getRestaurantId()))
                .orderDetail(OrderDetail.builder()
                        .products(restaurantProducts)
                        .build())
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval){
        return OrderApprovalEntity.builder()
                .id(orderApproval.getId().getValue())
                .restaurantId(orderApproval.getRestaurantId().getValue())
                .orderId(orderApproval.getOrderId().getValue())
                .status(orderApproval.getApprovalStatus())
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity){
        return OrderApproval.builder()
                .id(new OrderApprovalId(orderApprovalEntity.getId()))
                .restaurantId(new RestaurantId(orderApprovalEntity.getRestaurantId()))
                .orderId(new OrderId(orderApprovalEntity.getOrderId()))
                .approvalStatus(orderApprovalEntity.getStatus())
                .build();
    }

}
