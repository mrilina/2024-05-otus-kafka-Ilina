package org.carservice.reservation.web;

import org.carservice.reservation.MessagingCDCConfig;
import org.carservice.reservation.Reservation;
import org.carservice.reservation.TestContainersSetup;
import org.carservice.reservation.messaging.PaymentEvent;
import org.carservice.reservation.messaging.PaymentStatus;
import org.carservice.reservation.messaging.SpaceBookingEvent;
import org.carservice.reservation.messaging.SpaceBookingStatus;
import org.carservice.reservation.web.ReservationController.ReservationResource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Tag("system")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MessagingCDCConfig.class)
public class BookSpaceE2ETest extends TestContainersSetup {

    @BeforeEach
    void setUp(@Autowired TestRestTemplate restTemplate) throws IOException {
        registerTestConnector(restTemplate);
    }

    @Test
    @DisplayName("Place a parking space reservation")
    void makeAReservation(@Autowired TestRestTemplate restTemplate) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        // 1. start the process
        var resp = restTemplate.postForEntity("/v1/reservations", new HttpEntity<>(requestJSON(), headers), String.class);

        // 2. ensure mocking of parking service
        String sagaId; // message key for saga
        try (var consumer = kafkaConsumer()) {
            consumer.subscribe(List.of("space-booking.inbox.events"));
            var changeEvent = drain(consumer, 1).get(0);

            sagaId = changeEvent.key();
            consumer.unsubscribe();
        }

        try (KafkaProducer<String, SpaceBookingEvent> spaceBookingProducer = kafkaProducer()) {
            var record = new ProducerRecord<>("space-booking.outbox.events", sagaId, new SpaceBookingEvent(SpaceBookingStatus.BOOKED));
            enrichHeaders(record.headers(), "SpaceUpdated", "space-booking");

            spaceBookingProducer.send(record);
            spaceBookingProducer.flush();
        }

        // 3. ensure mocking of payment service
        try (var consumer = kafkaConsumer()) {
            consumer.subscribe(List.of("payment.inbox.events"));
            drain(consumer, 1);
            consumer.unsubscribe();
        }

        try (KafkaProducer<String, PaymentEvent> spaceBookingProducer = kafkaProducer()) {
            var record = new ProducerRecord<>("payment.outbox.events", sagaId, new PaymentEvent(PaymentStatus.REQUESTED));
            enrichHeaders(record.headers(), "PaymentUpdated", "payment");

            spaceBookingProducer.send(record);
            spaceBookingProducer.flush();
        }

        //4. check the reservation
        var reservationId = reservationIdFromLocationHeader(Objects.requireNonNull(resp.getHeaders().getLocation()));
        var expected = expected(reservationId);

        Awaitility.await()
                .atLeast(Duration.ofMillis(500))
                .atMost(Duration.ofSeconds(10))
                .with()
                .pollInterval(Duration.ofMillis(500))
                .until(() -> {
                    var reservation = restTemplate.getForEntity(resp.getHeaders().getLocation(), ReservationResource.class);
                    return expected.equals(reservation.getBody());
                });
    }

    private ReservationResource expected(UUID reservationId) {
        return ReservationResource.builder()
                .reservationId(reservationId)
                .parkingId(1L)
                .spaceId(1L)
                .guestId(10000001L)
                .status(Reservation.Status.SUCCEED)
                .build();
    }

    private UUID reservationIdFromLocationHeader(URI location) {
        var path = location.getPath();
        var lastIndex = path.lastIndexOf('/') + 1;
        return UUID.fromString(path.substring(lastIndex));
    }

    private void enrichHeaders(Headers record, String eventType, String aggregateType) {
        record.add(new RecordHeader("id", randomUUID().toString().getBytes()))
                .add(new RecordHeader("eventType", eventType.getBytes()))
                .add(new RecordHeader("aggregateType", aggregateType.getBytes()));
    }

    private String requestJSON() {
        return """
                {
                  "parkingId": 1,
                  "spaceId": 1,
                  "startDate": "2023-12-16",
                  "endDate": "2023-12-17",
                  "guestId": 10000001,
                  "paymentDue": 1702632793441,
                  "creditCardNo": "************7999"
                }
                """;
    }
}
