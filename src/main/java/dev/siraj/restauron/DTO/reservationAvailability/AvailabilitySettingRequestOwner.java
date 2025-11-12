package dev.siraj.restauron.DTO.reservationAvailability;

import dev.siraj.restauron.entity.restaurant.management.reservation.Slots;
import lombok.Data;

import java.util.List;

@Data
public class AvailabilitySettingRequestOwner {

    private String day;

    private List<Slots> slots;

}
