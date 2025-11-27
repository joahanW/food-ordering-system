package com.metrodata.restaurant.service.domain.application.ports.output.repository;

import com.metrodata.restaurant.service.domain.core.entity.OrderApproval;

public interface OrderApprovalRepository{
    OrderApproval save (OrderApproval orderApproval);
}
