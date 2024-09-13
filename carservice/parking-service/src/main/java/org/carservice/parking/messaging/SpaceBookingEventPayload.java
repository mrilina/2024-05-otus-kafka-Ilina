package org.carservice.parking.messaging;

import java.time.LocalDate;

public record SpaceBookingEventPayload(Integer parkingId,
                                       Integer spaceId,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       SpaceBookingRequestType type) {

    public boolean isRequestType() {
        return type.isRequestType();
    }
}
