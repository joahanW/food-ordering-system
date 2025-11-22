package com.metrodata.payment.service.domain.core.entity;

import com.metrodata.common.domain.entity.AggregateRoot;
import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.common.domain.valueobject.PaymentStatus;
import com.metrodata.payment.service.domain.core.valueobject.PaymentId;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
public class Payment extends AggregateRoot<PaymentId> {

    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money price;

    private PaymentStatus paymentStatus;
    private ZonedDateTime createdAt;

    public void initializePayment() {
        setId(new PaymentId(UUID.randomUUID()));
        createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public void validatePayment(List<String> failureMessages){
        if(price == null || !price.isGreaterThanZero()){
            failureMessages.add("Total price must be greater than Zero!!");
        }
    }

    public void updateStatus(PaymentStatus paymentStatus){
        this.paymentStatus = paymentStatus;
    }

}
