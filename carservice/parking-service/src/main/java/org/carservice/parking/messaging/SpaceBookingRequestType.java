package org.carservice.parking.messaging;

public enum SpaceBookingRequestType {
    REQUEST, CANCEL;

    public boolean isRequestType() {
        return this == REQUEST;
    }

}
