package dev.siraj.restauron.service.reservation.reservationAvailability.weeklyAvailabilityService;

import dev.siraj.restauron.DTO.reservationAvailability.AvailabilitySettingRequestOwner;
import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WeeklyAvailabilityService {
    @Transactional
    void saveWeeklyAvailability(String restaurantId, List<AvailabilitySettingRequestOwner> week);

    List<WeeklyAvailability> getWeeklyAvailability(String restaurantId);


}
