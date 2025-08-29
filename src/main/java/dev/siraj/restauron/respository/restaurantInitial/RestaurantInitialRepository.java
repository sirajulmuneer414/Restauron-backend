package dev.siraj.restauron.respository.restaurantInitial;

import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantInitialRepository extends JpaRepository<RestaurantRegistration, Long>, JpaSpecificationExecutor<RestaurantRegistration> {


    RestaurantRegistration findByOwnerEmail(String email);

    boolean existsByOwnerEmail(String email);
}
