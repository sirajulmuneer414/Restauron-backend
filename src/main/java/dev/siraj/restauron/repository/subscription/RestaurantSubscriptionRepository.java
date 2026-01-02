package dev.siraj.restauron.repository.subscription;

import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.subscription.RestaurantSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Repository interface for RestaurantSubscription entity

@Repository
public interface RestaurantSubscriptionRepository extends JpaRepository<RestaurantSubscription, Long> {

    List<RestaurantSubscription> findByStatusAndEndDateBefore(SubscriptionStatus subscriptionStatus, LocalDate today);

    @Query("SELECT s FROM RestaurantSubscription s WHERE s.status = 'ACTIVE' AND s.endDate = :targetDate AND s.reminder5DaysSent = :reminderSent")
    List<RestaurantSubscription> findExpiringSubscriptions(LocalDate targetDate, boolean reminderSent);

    Optional<RestaurantSubscription> findFirstByRestaurantIdAndStatusOrderByEndDateDesc(Long restaurantId, SubscriptionStatus status);

    Optional<RestaurantSubscription> findByRestaurant_IdAndStatus(Long restaurantId, SubscriptionStatus subscriptionStatus);
}
