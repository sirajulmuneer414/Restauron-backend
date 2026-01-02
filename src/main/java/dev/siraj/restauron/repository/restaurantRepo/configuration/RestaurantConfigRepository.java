package dev.siraj.restauron.repository.restaurantRepo.configuration;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.config.RestaurantConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository interface for Restaurant Configuration

@Repository
public interface RestaurantConfigRepository extends JpaRepository<RestaurantConfig, Long> {
    Optional<RestaurantConfig> findByRestaurant_Id(Long restaurantId);

    Optional<RestaurantConfig> findByRestaurant(Restaurant restaurant);
}
