package com.metrodata.restaurant.service.domain.core.entity;

import com.metrodata.common.domain.entity.BaseEntity;
import com.metrodata.common.domain.valueobject.OrderApprovalStatus;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.restaurant.service.domain.core.valueobject.OrderApprovalId;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalStatus approvalStatus;
}
