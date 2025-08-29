package dev.siraj.restauron.mapping.owner;

import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import org.springframework.stereotype.Component;

@Component
public class OwnerUserMapping {


    public Owner mapToOwnerFromUserAllAndRestaurantRegistration(UserAll userAll, RestaurantRegistration restaurantRegistration) {

        Owner owner = new Owner();

        owner.setUser(userAll);
        owner.setAdhaarNo(restaurantRegistration.getOwnerAdhaarNo());
        owner.setAdhaarPhoto(restaurantRegistration.getOwnerAdhaarPhoto());

        return owner;

    }
}
