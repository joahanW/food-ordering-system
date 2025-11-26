package com.metrodata.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
