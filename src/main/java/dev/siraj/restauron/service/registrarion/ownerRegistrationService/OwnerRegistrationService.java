package dev.siraj.restauron.service.registrarion.ownerRegistrationService;

import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;

public interface OwnerRegistrationService {
    Owner mapToOwnerAndSave(UserAll userAll, RestaurantRegistration restaurantRegistration);

    Owner findOwnerByUserAll(UserAll user);
}
