package dev.siraj.restauron.service.registrarion.restaurantRegistrationService;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;

public interface RestaurantRegistrationService {
    Restaurant registerRestaurantByRestaurantRegistrationDetailsAndOwner(RestaurantRegistration restaurantRegistration, Owner owner);


}
