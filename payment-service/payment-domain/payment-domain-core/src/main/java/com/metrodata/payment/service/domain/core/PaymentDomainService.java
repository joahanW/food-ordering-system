package com.metrodata.payment.service.domain.core;

import com.metrodata.common.domain.event.publisher.DomainEventPublisher;
import com.metrodata.payment.service.domain.core.entity.CreditEntry;
import com.metrodata.payment.service.domain.core.entity.CreditHistory;
import com.metrodata.payment.service.domain.core.entity.Payment;
import com.metrodata.payment.service.domain.core.event.PaymentCancelledEvent;
import com.metrodata.payment.service.domain.core.event.PaymentCompletedEvent;
import com.metrodata.payment.service.domain.core.event.PaymentEvent;
import com.metrodata.payment.service.domain.core.event.PaymentFailedEvent;

import java.util.ArrayList;
import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            ArrayList<CreditHistory> creditHistories,
                                            List<String> failureMessages);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          ArrayList<CreditHistory> creditHistories,
                                          List<String> failureMessages);
}
