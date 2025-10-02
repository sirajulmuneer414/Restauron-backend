package dev.siraj.restauron.service.restaurantService.restaurantServiceInterface;

import dev.siraj.restauron.DTO.customer.PublicViewRestaurantDto;
import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Owner;

public interface RestaurantService {
    Restaurant findRestaurantByRestaurantCode(String restaurantCode);

    Restaurant findRestaurantByOwner(Owner owner);

    RestaurantReduxSettingDto findRestaurantByOwnerFromEncryptedId(String ownerUserId);

    PublicViewRestaurantDto getPublicRestaurantDetailsUsingEncryptedId(String encryptedId);


}
