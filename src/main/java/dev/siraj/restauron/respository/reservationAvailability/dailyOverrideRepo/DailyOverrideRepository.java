package dev.siraj.restauron.respository.reservationAvailability.dailyOverrideRepo;

import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyOverrideRepository extends JpaRepository<DailyOverride, Long> {

    void deleteAllByRestaurantId(String restaurantId);

    List<DailyOverride> findAllByRestaurantId(String restaurantId);

    void deleteByDateLessThan(String string);
}
