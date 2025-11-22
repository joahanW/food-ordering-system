package com.metrodata.payment.service.domain.application.ports.output.repository;

import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.payment.service.domain.core.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);
    Optional<Payment> findByOrderId(OrderId orderId);

}
