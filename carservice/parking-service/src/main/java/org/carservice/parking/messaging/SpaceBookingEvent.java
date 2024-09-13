package org.carservice.parking.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.carservice.outbox.OutboxEvent;

import java.time.Instant;
import java.util.UUID;

public final class SpaceBookingEvent implements OutboxEvent<UUID, JsonNode> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final UUID sagaId;
    private final Instant timestamp;
    private final JsonNode payload;

    private SpaceBookingEvent(UUID sagaId, ObjectNode payload) {
        this.sagaId = sagaId;
        this.timestamp = Instant.now();
        this.payload = payload;
    }

    static SpaceBookingEvent of(UUID sagaId, SpaceBookingRequestStatus status) {
        var payload = mapper.createObjectNode()
                .put("status", status.name());

        return new SpaceBookingEvent(sagaId, payload);
    }

    @Override
    public UUID aggregateId() {
        return sagaId;
    }

    @Override
    public String aggregateType() {
        return "space-booking";
    }

    @Override
    public String type() {
        return "SpaceUpdated";
    }

    @Override
    public Instant timestamp() {
        return timestamp;
    }

    @Override
    public JsonNode payload() {
        return payload;
    }
}
