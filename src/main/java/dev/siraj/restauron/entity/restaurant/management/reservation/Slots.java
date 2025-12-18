package dev.siraj.restauron.entity.restaurant.management.reservation;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Slots {
    private String slotFrom;
    private String slotTo;

    private int maxReservation;

    private int maxNoOfPeoplePerReservation;
}
