package com.metrodata.payment.service.domain.application;

import com.metrodata.common.domain.valueobject.CustomerId;
import com.metrodata.common.domain.valueobject.OrderId;
import com.metrodata.common.domain.valueobject.PaymentStatus;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.payment.service.domain.application.dto.PaymentRequest;
import com.metrodata.payment.service.domain.application.exception.PaymentApplicationServiceException;
import com.metrodata.payment.service.domain.application.mapper.PaymentDataMapper;
import com.metrodata.payment.service.domain.application.outbox.model.OrderOutboxMessage;
import com.metrodata.payment.service.domain.application.outbox.scheduler.OrderOutboxHelper;
import com.metrodata.payment.service.domain.application.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.metrodata.payment.service.domain.application.ports.output.repository.CreditEntryRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.CreditHistoryRepository;
import com.metrodata.payment.service.domain.application.ports.output.repository.PaymentRepository;
import com.metrodata.payment.service.domain.core.PaymentDomainService;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;
import com.metrodata.payment.service.domain.core.entity.Payment;
import com.metrodata.payment.service.domain.core.event.PaymentEvent;
import com.metrodata.payment.service.domain.core.exception.PaymentNotFoundException;
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
    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @Transactional
    public void persistPayment(PaymentRequest paymentRequest) {
        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info("An outbox message with saga id: {} is already saved to database",
                    paymentRequest.getSagaId());
            return;
        }

        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        ArrayList<CreditHistory> creditHistories = new ArrayList<>(getCreditHistory(payment.getCustomerId()));
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent =
                paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));
    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {
        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info("An outbox message with saga id: {} is already saved to database!",
                    paymentRequest.getSagaId());
            return;
        }

        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentResponse = paymentRepository
                .findByOrderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())));
        if (paymentResponse.isEmpty()) {
            log.error("Payment with order id: {} could not be found!", paymentRequest.getOrderId());
            throw new PaymentNotFoundException("Payment with order id: " +
                    paymentRequest.getOrderId() + " could not be found!");
        }
        Payment payment = paymentResponse.get();
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        ArrayList<CreditHistory> creditHistories = new ArrayList<>(getCreditHistory(payment.getCustomerId()));
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService
                .validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

        orderOutboxHelper.saveOrderOutboxMessage(paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSagaId()));

    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " +
                    customerId.getValue());
        }
        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()) {
            log.error("Could not find credit history for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit history for customer: " +
                    customerId.getValue());
        }
        return creditHistories.get();
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private boolean publishIfOutboxMessageProcessedForPayment(PaymentRequest paymentRequest,
                                                              PaymentStatus paymentStatus) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                        UUID.fromString(paymentRequest.getSagaId()),
                        paymentStatus);
        if (orderOutboxMessage.isPresent()) {
            paymentResponseMessagePublisher.publish(orderOutboxMessage.get(), orderOutboxHelper::updateOutboxMessage);
            return true;
        }
        return false;
    }

}
