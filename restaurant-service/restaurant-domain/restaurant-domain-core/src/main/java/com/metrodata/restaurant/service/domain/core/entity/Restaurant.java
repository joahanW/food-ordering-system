package com.metrodata.restaurant.service.domain.core.entity;

import com.metrodata.common.domain.entity.AggregateRoot;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.OrderApprovalStatus;
import com.metrodata.common.domain.valueobject.OrderStatus;
import com.metrodata.common.domain.valueobject.RestaurantId;
import com.metrodata.restaurant.service.domain.core.valueobject.OrderApprovalId;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
public class Restaurant extends AggregateRoot<RestaurantId> {
    private OrderApproval orderApproval;
    private boolean active;
    private final OrderDetail orderDetail;

    public void validateOrder(List<String> failureMessages){
        if (orderDetail.getOrderStatus() != OrderStatus.PAID){
            failureMessages.add(String.format("Payment is not completed for order:  %s is not available",
                    orderDetail.getId().getValue()));
        }
        Money totalAmount = orderDetail.getProducts().stream().map(product -> {
            if (!product.isAvailable()) {
                failureMessages.add(String.format("Product with id: %s is not available",
                        product.getId().getValue()));
            }
            return product.getPrice().multiply(product.getQuantity());
        }).reduce(Money.ZERO, Money::add);
        if (!totalAmount.equals(orderDetail.getTotalAmount())){
            failureMessages.add(String.format("Price total is not correct for order: %s",
                    orderDetail.getId()));
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus){
        this.orderApproval = OrderApproval.builder()
                .id(new OrderApprovalId(UUID.randomUUID()))
                .restaurantId(this.getId())
                .orderId(this.getOrderDetail().getId())
                .approvalStatus(orderApprovalStatus)
                .build();
    }
}
