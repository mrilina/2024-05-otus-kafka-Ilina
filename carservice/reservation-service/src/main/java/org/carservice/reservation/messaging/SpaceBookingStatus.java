package org.carservice.reservation.messaging;


import org.carservice.reservation.framework.SagaStepStatus;

public enum SpaceBookingStatus {
    BOOKED, REJECTED, CANCELLED;

    public SagaStepStatus toSagaStepStatus() {
        return switch (this) {
            case BOOKED -> SagaStepStatus.SUCCEEDED;
            case REJECTED -> SagaStepStatus.FAILED;
            case CANCELLED -> SagaStepStatus.COMPENSATED;
        };
    }
}
