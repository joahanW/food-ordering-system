package com.metrodata.saga;

import com.metrodata.common.domain.event.DomainEvent;

public interface SagaStep <T>{
    void process(T data);
    void rollback(T data);
}
