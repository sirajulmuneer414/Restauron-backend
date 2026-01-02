package dev.siraj.restauron.repository.reservation.reservationAvailability.dailyOverrideRepo;

import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyOverrideRepository extends JpaRepository<DailyOverride, Long> {

    void deleteAllByRestaurantId(String restaurantId);

    List<DailyOverride> findAllByRestaurantId(String restaurantId);

    void deleteByDateLessThan(String string);

    Optional<DailyOverride> findByRestaurantIdAndDate(String encryptedRestaurantId, String date);
}
