package com.metrodata.common.domain.event;

public interface DomainEvent<T>{
    void fire();
}
