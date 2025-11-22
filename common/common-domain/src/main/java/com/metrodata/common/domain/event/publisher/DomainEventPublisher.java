package com.metrodata.common.domain.event.publisher;

import com.metrodata.common.domain.event.DomainEvent;

public interface DomainEventPublisher <T extends DomainEvent> {
    void publish(T domainEvent);
}
