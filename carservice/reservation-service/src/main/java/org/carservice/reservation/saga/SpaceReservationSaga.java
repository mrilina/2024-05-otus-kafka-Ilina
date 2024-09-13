package org.carservice.reservation.saga;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.carservice.reservation.Reservation;
import org.carservice.reservation.Reservations;
import org.carservice.reservation.framework.Saga;
import org.carservice.reservation.framework.SagaState;
import org.carservice.reservation.framework.SagaStepStatus;
import org.carservice.reservation.messaging.PaymentEvent;
import jakarta.persistence.EntityManager;
import org.carservice.reservation.messaging.SpaceBookingEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.carservice.reservation.framework.Saga.PayloadType.CANCEL;
import static org.carservice.reservation.framework.Saga.PayloadType.REQUEST;
import static org.carservice.reservation.framework.SagaStepStatus.COMPENSATING;
import static org.carservice.reservation.framework.SagaStepStatus.STARTED;
import static org.carservice.reservation.saga.SpaceReservationSaga.SagaStateOrder.PAYMENT;
import static org.carservice.reservation.saga.SpaceReservationSaga.SagaStateOrder.SPACE_BOOKING;

public final class SpaceReservationSaga extends Saga {

    private final ApplicationEventPublisher eventPublisher;
    private final Reservations reservations;
    private final SagaState state;

    SpaceReservationSaga(ApplicationEventPublisher eventPublisher,
                         EntityManager entityManager,
                         Reservations reservations,
                         SagaState state) {
        super(entityManager);
        this.eventPublisher = eventPublisher;
        this.reservations = reservations;
        this.state = state;
    }

    /**
     * Init Saga
     */
    public void init() {
        advance();
    }

    /**
     * handle Payment Event
     *
     * @param eventId - ensure one-time handling, consumed skip
     * @param payload
     */
    public void onPaymentEvent(UUID eventId, PaymentEvent payload) {
        ensureProcessed(eventId, () -> {
            onStepEvent(PAYMENT.topic, payload.status().toSagaStepStatus());
            updateBookingStatus();
        });
    }

    public void onSeatBookingEvent(UUID eventId, SpaceBookingEvent payload) {
        ensureProcessed(eventId, () -> {
            onStepEvent(SPACE_BOOKING.topic, payload.status().toSagaStepStatus());
            updateBookingStatus();
        });
    }

    private void updateBookingStatus() {
        var booking = reservations.findById(new Reservation.ReservationIdentifier(getBookingId()))
                .orElseThrow(RuntimeException::new);

        if (state.sagaStatus().isCompleted()) {
            booking.markSucceed();
        } else if (state.sagaStatus().isAborted()) {
            booking.markFailed();
        }
    }

    private UUID getBookingId() {
        return UUID.fromString(state.payload().get("reservationId").asText());
    }

    private void onStepEvent(String step, SagaStepStatus status) {
        state.updateStepStatus(step, status);

        if (status.isSucceeded()) {
            advance();
        } else if (status.isFailedOrCompensated()) {
            goBack();
        }

        state.advanceSagaStatus();
    }

    private void advance() {
        var next = SagaStateOrder.next(state.currentStep());
        if (next == null) {
            state.currentStep(null);
            return;
        }

        eventPublisher.publishEvent(new SagaEvent(state.id(), next.topic, REQUEST.name(), state.payload()));

        state.updateStepStatus(next.topic, STARTED);
        state.currentStep(next.topic);
    }

    private void goBack() {
        var prev = SagaStateOrder.prev(state.currentStep());
        if (prev == null) {
            state.currentStep(null);
            return;
        }

        var payload = ((ObjectNode) state.payload().deepCopy());
        payload.put("type", CANCEL.name());

        eventPublisher.publishEvent(new SagaEvent(state.id(), prev.topic, CANCEL.name(), payload));

        state.updateStepStatus(prev.topic, COMPENSATING);
        state.currentStep(prev.topic);
    }

    /**
     * Saga State Machine
     */
    enum SagaStateOrder {
        SPACE_BOOKING("space-booking") {
            @Override
            public SagaStateOrder next() {
                return PAYMENT;
            }

            @Override
            public SagaStateOrder prev() {
                return null;
            }
        },
        PAYMENT("payment") {
            @Override
            public SagaStateOrder next() {
                return null;
            }

            @Override
            public SagaStateOrder prev() {
                return SPACE_BOOKING;
            }
        };

        public final String topic;

        SagaStateOrder(String topic) {
            this.topic = topic;
        }

        abstract SagaStateOrder next();

        abstract SagaStateOrder prev();

        static SagaStateOrder startStep() {
            return SPACE_BOOKING;
        }

        static SagaStateOrder next(String topic) {
            if (topic == null) {
                return startStep();
            }

            for (var t : values()) {
                if (t.topic.equals(topic))
                    return t.next();
            }

            return null;
        }

        static SagaStateOrder prev(String topic) {
            if (topic == null) {
                return null;
            }

            for (var t : values()) {
                if (t.topic.equals(topic))
                    return t.prev();
            }

            return null;
        }
    }
}
