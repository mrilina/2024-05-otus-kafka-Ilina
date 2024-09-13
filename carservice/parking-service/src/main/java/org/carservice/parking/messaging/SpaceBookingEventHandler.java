package org.carservice.parking.messaging;

import org.carservice.parking.Space;
import org.carservice.parking.Spaces;
import org.carservice.parking.messaging.log.EventLogs;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
class SpaceBookingEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(SpaceBookingEventHandler.class);

    private final ApplicationEventPublisher eventPublisher;
    private final EventLogs eventLogs;
    private final Spaces spaces;

    void onBookSeatEvent(UUID sagaId, UUID eventId, SpaceBookingEventPayload payload) {
        if (eventLogs.alreadyProcessed(eventId)) {
            logger.debug("Event with UUID {} was already retrieved, ignoring it", eventId);
            return;
        }

        var possibleSpace = spaces.findById(new Space.SpaceIdentifier(payload.spaceId()));

        final SpaceBookingRequestStatus status;
        if (payload.isRequestType() || possibleSpace.isEmpty()) {
            if (possibleSpace.isEmpty() || possibleSpace.get().isBooked()) {
                status = SpaceBookingRequestStatus.REJECTED;
            } else {
                possibleSpace.get().book();
                status = SpaceBookingRequestStatus.BOOKED;
            }
        } else {
            possibleSpace.get().release();
            status = SpaceBookingRequestStatus.CANCELLED;
        }

        eventPublisher.publishEvent(SpaceBookingEvent.of(sagaId, status));
        eventLogs.processed(eventId);
    }
}
