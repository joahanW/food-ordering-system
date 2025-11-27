package com.metrodata.restaurant.service.dataaccess.adapter;

import com.metrodata.restaurant.service.domain.application.ports.output.repository.OrderApprovalRepository;
import com.metrodata.restaurant.service.dataaccess.mapper.RestaurantDataAccessMapper;
import com.metrodata.restaurant.service.dataaccess.repository.OrderApprovalJpaRepository;
import com.metrodata.restaurant.service.domain.core.entity.OrderApproval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return restaurantDataAccessMapper
            .orderApprovalEntityToOrderApproval(orderApprovalJpaRepository
                    .save(restaurantDataAccessMapper
                    .orderApprovalToOrderApprovalEntity(orderApproval)
            ));
    }

}
