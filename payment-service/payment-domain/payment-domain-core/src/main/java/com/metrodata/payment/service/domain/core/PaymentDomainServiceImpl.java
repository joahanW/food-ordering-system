package com.metrodata.payment.service.domain.core;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.common.domain.valueobject.Money;
import com.metrodata.common.domain.valueobject.PaymentStatus;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;
import com.metrodata.payment.service.domain.core.entity.Payment;
import com.metrodata.payment.service.domain.core.event.PaymentCancelledEvent;
import com.metrodata.payment.service.domain.core.event.PaymentCompletedEvent;
import com.metrodata.payment.service.domain.core.event.PaymentEvent;
import com.metrodata.payment.service.domain.core.event.PaymentFailedEvent;
import com.metrodata.payment.service.domain.core.valueobject.CreditHistoryId;
import com.metrodata.payment.service.domain.core.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.metrodata.common.domain.DomainConstant.UTC;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment,
                                                   CreditEntry creditEntry,
                                                   ArrayList<CreditHistory> creditHistories,
                                                   List<String> failureMessages) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)),failureMessages);
        }else {
            log.info("Payment initiation is failed for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment,
                                                 CreditEntry creditEntry,
                                                 ArrayList<CreditHistory> creditHistories,
                                                 List<String> failureMessages) {
        payment.validatePayment(failureMessages);
        creditEntry.addCreditAmount(payment.getPrice());
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);
        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id: {}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages );
        }else {
            log.info("Payment cancellation is failed for order id :{}", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages);
        }
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())){
            log.error("Customer with id: {} doesn't have enough credit for payment",
                    payment.getCustomerId().getValue());
            failureMessages.add("Customer with id: " + payment.getCustomerId().getValue() +
                    " doesn't have enough credit for payment!");
        }
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry){
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(Payment payment,
                                                    ArrayList<CreditHistory> creditHistories,
                                                    TransactionType transactionType){
        creditHistories.add(CreditHistory.builder()
                        .id(new CreditHistoryId(UUID.randomUUID()))
                        .customerId(payment.getCustomerId())
                        .amount(payment.getPrice())
                        .transactionType(transactionType)
                .build());
    }

    private void validateCreditHistory(CreditEntry creditEntry,
                                       List<CreditHistory> creditHistories,
                                       List<String> failureMessages){
        Money totalCreditHistory = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistory = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);
        if (totalDebitHistory.isGreaterThan(totalCreditHistory)){
            log.error("Customer with id: {} doesn't have enough credit according to credit history",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add("Customer with id: " + creditEntry.getCustomerId() +
                        "  doesn't have enough credit according to credit history!");
        }

        // Validation: Mendeteksi ketidaksesuaian antara saldo credit dengan riwayat transaksi
        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))){
            log.error("Credit history total is not equal to current credit for customer id: {}!",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add("Credit history total is not equal to current credit for customer id: " +
                    creditEntry.getCustomerId().getValue());
        }
    }

    private Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType){
        return creditHistories.stream()
                .filter(creditHistory -> transactionType == creditHistory.getTransactionType())
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

}
