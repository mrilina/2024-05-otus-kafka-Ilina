package org.carservice.outbox;

import java.time.Instant;

public interface OutboxEvent<ID, P> {

    ID aggregateId();

    String aggregateType();

    String type();

    Instant timestamp();

    P payload();
}
