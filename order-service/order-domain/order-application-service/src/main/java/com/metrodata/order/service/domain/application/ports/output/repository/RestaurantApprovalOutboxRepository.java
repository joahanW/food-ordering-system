package com.metrodata.order.service.domain.application.ports.output.repository;

import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalOutboxMessage;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantApprovalOutboxRepository {
    OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage);
    Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type,
                                                                                      OutboxStatus outboxStatus,
                                                                                      SagaStatus... sagaStatus);
    Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                          UUID sagaId,
                                                                          SagaStatus... sagaStatuses);
    void deleteByTypeAndOutboxStatusAndSagaStatus(String type,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatus);
}
