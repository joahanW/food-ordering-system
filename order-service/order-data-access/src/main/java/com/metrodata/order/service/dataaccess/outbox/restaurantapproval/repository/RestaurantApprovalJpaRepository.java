package com.metrodata.order.service.dataaccess.outbox.restaurantapproval.repository;

import com.metrodata.order.service.dataaccess.outbox.restaurantapproval.entity.RestaurantApprovalOutboxEntity;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalOutboxMessage;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.saga.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantApprovalJpaRepository extends JpaRepository<RestaurantApprovalOutboxEntity, UUID> {
    Optional<List<RestaurantApprovalOutboxEntity>> findByTypeAndOutboxStatusAndSagaStatusIn(String type,
                                                                                        OutboxStatus outboxStatus,
                                                                                        List<SagaStatus> sagaStatus);

    Optional<RestaurantApprovalOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(String type,
                                                                      UUID sagaId,
                                                                      List<SagaStatus> sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(String type,
                                                    OutboxStatus outboxStatus,
                                                    List<SagaStatus> sagaStatus);

}
