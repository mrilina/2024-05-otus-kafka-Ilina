package org.carservice.reservation.saga;

import org.carservice.reservation.Reservation;
import org.carservice.reservation.Reservations;
import org.carservice.reservation.framework.Saga;
import org.carservice.reservation.framework.SagaState;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaManager {

    private final ApplicationEventPublisher eventPublisher;
    private final EntityManager entityManager;
    private final Reservations reservations;

    public void begin(Reservation reservation) {
        var payload = reservation.toSagaPayload()
                .put("type", Saga.PayloadType.REQUEST.name());

        var sagaState = new SagaState("space-reservation", payload);
        entityManager.persist(sagaState);

        var saga = new SpaceReservationSaga(eventPublisher, entityManager, reservations, sagaState);
        saga.init();
    }

    public SpaceReservationSaga find(UUID sagaId) {
        var state = entityManager.find(SagaState.class, sagaId);

        if (state == null) {
            return null;
        }

        return new SpaceReservationSaga(eventPublisher, entityManager, reservations, state);
    }
}
