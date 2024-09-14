package org.carservice.reservation;

import org.carservice.reservation.saga.SagaManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SpaceReservationUseCase {

    private final SagaManager sagaManager;
    private final Reservations reservations;

    public Reservation make(SpaceReservationCmd cmd) {
        var reservation = reservations.save(cmd.toReservation());
        sagaManager.begin(reservation);
        return reservation;
    }
}
