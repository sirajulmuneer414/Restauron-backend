package dev.siraj.restauron.respository.reservationAvailability.weeklyAvailabiliyRepo;

import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyAvailabilityRepository extends JpaRepository<WeeklyAvailability, Long> {


    void deleteAllByRestaurantId(String restaurantId);

    List<WeeklyAvailability> findAllByRestaurantId(String restaurantId);
}
