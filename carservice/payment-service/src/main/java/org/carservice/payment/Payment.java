package org.carservice.payment;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = PRIVATE, force = true)
@ToString
public class Payment {

    @Id
    public final UUID reservationId;

    public final Integer guestId;

    public final Long paymentDue;

    public final String creditCardNo;

    @Enumerated(EnumType.STRING)
    public PaymentRequestType type;

    public PaymentStatus paymentStatus() {
        if (type == null || creditCardNo == null) {
            return PaymentStatus.FAILED;
        }

        PaymentStatus status;
        if (type.isRequest()) {
            if (creditCardNo.endsWith("9999")) {
                status = PaymentStatus.FAILED;
            } else {
                status = PaymentStatus.REQUESTED;
            }
        } else {
            status = PaymentStatus.CANCELLED;
        }

        return status;
    }
}
