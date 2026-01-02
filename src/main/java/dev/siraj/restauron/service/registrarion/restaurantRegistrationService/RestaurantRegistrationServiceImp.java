package dev.siraj.restauron.service.registrarion.restaurantRegistrationService;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RestaurantRegistrationServiceImp implements RestaurantRegistrationService{

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantRegistrationMapping restaurantRegistrationMapping;

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private Restaurant save(Restaurant restaurant){

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant registerRestaurantByRestaurantRegistrationDetailsAndOwner(RestaurantRegistration restaurantRegistration, Owner owner) {

        Restaurant restaurant = restaurantRegistrationMapping.mapRestaurantRegistrationEntityToRestaurantEntity(restaurantRegistration, owner);

        Restaurant savedRestaurant =  save(restaurant);

        String encryptedId = idEncryptionService.encryptLongId(savedRestaurant.getId());

        String customerUrl = frontendUrl + "/restaurant/" + encryptedId + "home";

        savedRestaurant.setCustomerPageUrl(customerUrl);

        return save(savedRestaurant);

    }


}
