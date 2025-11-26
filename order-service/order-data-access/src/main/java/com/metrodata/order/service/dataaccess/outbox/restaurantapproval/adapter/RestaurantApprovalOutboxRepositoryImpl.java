package com.metrodata.order.service.dataaccess.outbox.restaurantapproval.adapter;

import com.metrodata.common.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.metrodata.order.service.dataaccess.outbox.restaurantapproval.mapper.RestaurantApprovalOutboxDataAccessMapper;
import com.metrodata.order.service.dataaccess.outbox.restaurantapproval.repository.RestaurantApprovalJpaRepository;
import com.metrodata.order.service.domain.application.outbox.model.approval.OrderApprovalOutboxMessage;
import com.metrodata.order.service.domain.application.ports.output.repository.RestaurantApprovalOutboxRepository;
import com.metrodata.outbox.OutboxStatus;
import com.metrodata.saga.SagaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestaurantApprovalOutboxRepositoryImpl implements RestaurantApprovalOutboxRepository {

    private final RestaurantApprovalJpaRepository restaurantApprovalJpaRepository;
    private final RestaurantApprovalOutboxDataAccessMapper restaurantApprovalOutboxDataAccessMapper;

    @Override
    public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        return restaurantApprovalOutboxDataAccessMapper
                .approvalOutboxEntityToOrderApprovalOutboxMessage(restaurantApprovalJpaRepository
                        .save(restaurantApprovalOutboxDataAccessMapper
                                .orderCreatedOutboxMessageToOutboxEntity(orderApprovalOutboxMessage)));
    }

    @Override
    public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType,
                                                                                             OutboxStatus outboxStatus,
                                                                                             SagaStatus... sagaStatus) {
        return Optional.of(restaurantApprovalJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus,
                        Arrays.asList(sagaStatus))
                .orElseThrow(() -> new RestaurantDataAccessException("Approval outbox object " +
                        "could be found for saga type " + sagaType))
                .stream()
                .map(restaurantApprovalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String type,
                                                                                 UUID sagaId,
                                                                                 SagaStatus... sagaStatus) {
        return restaurantApprovalJpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(type, sagaId,
                        Arrays.asList(sagaStatus))
                .map(restaurantApprovalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage);

    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        restaurantApprovalJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                Arrays.asList(sagaStatus));
    }

}
