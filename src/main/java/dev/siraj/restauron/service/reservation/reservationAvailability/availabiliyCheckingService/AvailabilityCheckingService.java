package dev.siraj.restauron.service.reservation.reservationAvailability.availabiliyCheckingService;

import dev.siraj.restauron.entity.restaurant.management.reservation.Slots;

import java.util.List;

public interface AvailabilityCheckingService {
    List<Slots> getSlotsForTheDate(String date, String encryptedRestaurantId);
}
