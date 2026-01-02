package dev.siraj.restauron.repository.reservation.reservationAvailability.weeklyAvailabiliyRepo;

import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyAvailabilityRepository extends JpaRepository<WeeklyAvailability, Long> {


    void deleteAllByRestaurantId(String restaurantId);

    List<WeeklyAvailability> findAllByRestaurantId(String restaurantId);

    Optional<WeeklyAvailability> findByRestaurantIdAndDayOfTheWeek(String encryptedRestaurantId, String dayOfWeek);
}
