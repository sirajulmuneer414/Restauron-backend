package dev.siraj.restauron.respository.restaurantRepo;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long>, JpaSpecificationExecutor<Restaurant> {


    Restaurant findByOwner(Owner owner);

    Optional<Restaurant> findByOwner_User_Id(Long ownerUserId);

    Optional<Restaurant> findById(Long id);
}
