package com.metrodata.restaurant.service.domain.core.valueobject;

import com.metrodata.common.domain.valueobject.BaseId;
import lombok.AllArgsConstructor;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
