package dev.siraj.restauron.service.restaurantService;

import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.ownerService.OwnerService;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantServiceImp implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private UserService userService;

    @Override
    public Restaurant findRestaurantByRestaurantCode(String restaurantCode) {

        return null;

    }

    @Override
    public Restaurant findRestaurantByOwner(Owner owner) {
        return restaurantRepository.findByOwner(owner);
    }

    @Override
    public RestaurantReduxSettingDto findRestaurantByOwnerFromEncryptedId(String ownerUserId) {
        Long userId = idEncryptionService.decryptToLongId(ownerUserId);

        UserAll user = userService.findUserById(userId);

        Owner owner = ownerService.findOwnerByUser(user);

        Restaurant restaurant = restaurantRepository.findByOwner(owner);

        RestaurantReduxSettingDto dto = new RestaurantReduxSettingDto();

        dto.setRestaurantName(restaurant.getName());
        dto.setRestaurantEncryptedId(idEncryptionService.encryptLongId(restaurant.getId()));

        return dto;
    }


}
