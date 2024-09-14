package org.carservice.parking;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "space")
@NoArgsConstructor(access = PRIVATE, force = true)
@ToString
public class Space {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private SpaceIdentifier id;

    /**
     * Дата создания.
     */
    private final Instant creationTime;

    /**
     * Наименование.
     */
    private String name;

    /**
     * Номер.
     */
    private Integer number;

    /**
     * Этаж.
     */
    private Integer floor;

    /**
     * Признак доступности.
     */
    private Boolean available;

    /**
     * Сведения о парковке.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    private Parking parking;

    public Space(SpaceIdentifier id, String name, Integer number, Integer floor, Parking parking) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.floor = floor;
        this.available = true;
        this.creationTime = Instant.now();
        this.parking = parking;
    }

    public boolean isBooked() {
        return !available;
    }

    public void book() {
        this.available = false;
    }

    public void release() {
        this.available = true;
    }

    @Embeddable
    public record SpaceIdentifier(Integer id) implements Serializable {

        public String toString() {
            return id.toString();
        }
    }
}
