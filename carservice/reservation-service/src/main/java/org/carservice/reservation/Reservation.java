package org.carservice.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = PRIVATE, force = true) // JPA compliant
public class Reservation {

    @Id
    private final ReservationIdentifier id;

    private Long parkingId;
    private Long spaceId;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long guestId;
    public Long paymentDue;
    public String creditCardNo;

    @Builder
    public Reservation(Long parkingId,
                       Long spaceId,
                       LocalDate startDate,
                       LocalDate endDate,
                       Long guestId,
                       Long paymentDue,
                       String creditCardNo) {
        this.id = new ReservationIdentifier(UUID.randomUUID());
        this.parkingId = requireNonNull(parkingId, "parkingId cannot be null");
        this.spaceId = requireNonNull(spaceId, "spaceId cannot be null");
        this.startDate = requireNonNull(startDate, "startDate cannot be null");
        this.endDate = requireNonNull(endDate, "startDate cannot be null");
        this.guestId = requireNonNull(guestId, "guestId cannot be null");
        this.paymentDue = paymentDue;
        this.creditCardNo = creditCardNo;
        this.status = Status.PENDING;
    }

    public ReservationIdentifier id() {
        return id;
    }

    public Status status() {
        return status;
    }

    public Long parkingId() {
        return parkingId;
    }

    public Long spaceId() {
        return spaceId;
    }

    public Long guestId() {
        return guestId;
    }

    public void markSucceed() {
        this.status = Status.SUCCEED;
    }

    public void markFailed() {
        this.status = Status.FAILED;
    }

    @Embeddable
    public record ReservationIdentifier(UUID id) implements Serializable {

        public String toString() {
            return id.toString();
        }
    }

    public enum Status {
        PENDING, SUCCEED, FAILED, CANCELED, REFUND
    }

    public ObjectNode toSagaPayload() {
        return new ObjectMapper().createObjectNode()
                .put("reservationId", id.toString())
                .put("parkingId", parkingId)
                .put("spaceId", spaceId)
                .put("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                .put("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                .put("guestId", guestId)
                .put("paymentDue", paymentDue)
                .put("creditCardNo", creditCardNo);
    }
}
