package dev.siraj.restauron.service.reservation.reservationAvailability.dailyOverrideService;

import dev.siraj.restauron.DTO.reservationAvailability.AvailabilitySettingRequestOwner;
import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DailyOverrideService {
    @Transactional
    void saveOverrides(String restaurantId, List<AvailabilitySettingRequestOwner> overridesList);

    List<DailyOverride> getOverrides(String restaurantId);
}
