package org.carservice.parking.messaging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_KEY;

@KafkaListener(
        topics = "${kafka.topic.spacebooking.inbox.events.name}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "spaceBookingKLCFactory"
)
@Component
@RequiredArgsConstructor
class SpaceBookingInboxEventsConsumer {

    private final Logger logger = LoggerFactory.getLogger(SpaceBookingInboxEventsConsumer.class);

    private final SpaceBookingEventHandler spaceBookingEventHandler;

    @KafkaHandler
    public void handle(@Header(RECEIVED_KEY) UUID sagaId,
                       @Header("id") String eventId,
                       @Header("eventType") String eventType,
                       @Payload SpaceBookingEventPayload payload) {
        logger.debug("Kafka message with key = {}, eventType {} arrived", sagaId, eventType);
        spaceBookingEventHandler.onBookSeatEvent(sagaId, UUID.fromString(eventId), payload);
    }
}
