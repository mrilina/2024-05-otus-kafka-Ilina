package org.carservice.parking;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

import static lombok.AccessLevel.PACKAGE;

@Entity
@Table(name = "parking")
@NoArgsConstructor(access = PACKAGE, force = true) // JPA compliant
@ToString
public class Parking {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ParkingIdentifier id;

    /**
     * Дата создания.
     */
    private final Instant creationTime;

    /**
     * Наименование.
     */
    private String name;

    /**
     * Адрес.
     */
    private String address;

    /**
     * Локация.
     */
    private String location;

    public Parking(ParkingIdentifier id, String name, String address, String location) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
        this.creationTime = Instant.now();
    }

    @Embeddable
    public record ParkingIdentifier(Integer id) implements Serializable {

        public String toString() {
            return id.toString();
        }
    }
}
