package dev.siraj.restauron.service.registrarion.restaurantRegistrationService;

import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantRegistrationServiceImp implements RestaurantRegistrationService{

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantRegistrationMapping restaurantRegistrationMapping;


    private Restaurant save(Restaurant restaurant){

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant registerRestaurantByRestaurantRegistrationDetailsAndOwner(RestaurantRegistration restaurantRegistration, Owner owner) {

        Restaurant restaurant = restaurantRegistrationMapping.mapRestaurantRegistrationEntityToRestaurantEntity(restaurantRegistration, owner);

        return save(restaurant);

    }


}
