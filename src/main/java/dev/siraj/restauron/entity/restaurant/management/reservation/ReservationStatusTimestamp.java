package dev.siraj.restauron.entity.restaurant.management.reservation;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ReservationStatusTimestamp {

    private String status;

    private String date;

    private String time;

    private String doneBy;
}
