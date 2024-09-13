package org.carservice.reservation;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SpaceReservationCmd(Long parkingId,
                                  Long spaceId,
                                  LocalDate startDate,
                                  LocalDate endDate,
                                  Long guestId,
                                  Long paymentDue,
                                  String creditCardNo) {

    Reservation toReservation() {
        return Reservation.builder()
                .parkingId(parkingId)
                .spaceId(spaceId)
                .startDate(startDate)
                .endDate(endDate)
                .guestId(guestId)
                .paymentDue(paymentDue)
                .creditCardNo(creditCardNo)
                .build();
    }
}