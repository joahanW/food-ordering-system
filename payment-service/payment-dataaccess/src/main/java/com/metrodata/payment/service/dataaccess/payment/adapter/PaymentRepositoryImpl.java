package com.metrodata.payment.service.dataaccess.payment.adapter;

import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.payment.service.dataaccess.payment.entity.PaymentEntity;
import com.metrodata.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import com.metrodata.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.PaymentRepository;
import com.metrodata.payment.service.domain.core.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    @Override
    public Payment save(Payment payment) {
        return paymentDataAccessMapper
                .paymentEntityToPayment(paymentJpaRepository
                        .save(paymentDataAccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return paymentJpaRepository.findByOrderId(orderId.getValue())
                .map(paymentDataAccessMapper::paymentEntityToPayment);
    }
}
