package com.metrodata.payment.service.domain.application;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.application.exception.PaymentApplicationServiceException;
import com.metrodata.payment.service.domain.application.mapper.PaymentDataMapper;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.repository.CreditEntryRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.CreditHistoryRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.PaymentRepository;
import com.metrodata.payment.service.domain.core.PaymentDomainService;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;
import com.metrodata.payment.service.domain.core.entity.Payment;
import com.metrodata.payment.service.domain.core.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
    private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest){
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService
                .validateAndInitiatePayment(
                        payment,
                        creditEntry,
                        creditHistories,
                        failureMessages,
                        paymentCompletedMessagePublisher,
                        paymentFailedMessagePublisher
                );
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest){
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Payment payment = getPayment(new OrderId(UUID.fromString(paymentRequest.getOrderId())));
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService
                .validateAndCancelPayment(
                        payment,
                        creditEntry,
                        creditHistories,
                        failureMessages,
                        paymentCancelledMessagePublisher,
                        paymentFailedMessagePublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);
        return paymentEvent;
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages){
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size()-1));
        }
    }

    private CreditEntry getCreditEntry(CustomerId customerId){
        return creditEntryRepository.findByCustomerId(customerId)
                .orElseThrow(() ->{
                    log.error("Could not find credit entry for customer: {}", customerId.getValue());
                    return new PaymentApplicationServiceException("Could not find credit entry for customer:" +
                            customerId.getValue());
                });
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId){
        return creditHistoryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                   log.error("Could not find credit history for customer: {}", customerId.getValue());
                   return new PaymentApplicationServiceException("Could not find credit history for customer:" +
                           customerId.getValue());
                });
    }

    private Payment getPayment(OrderId orderId){
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(()->{
                    log.error("Payment not found for order id: {}", orderId.getValue());
                    return new PaymentApplicationServiceException("Payment not found for order id: " + orderId.getValue());
                });
    }

}
