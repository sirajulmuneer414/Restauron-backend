package dev.siraj.restauron.service.restaurantService;

import dev.siraj.restauron.DTO.customer.PublicViewRestaurantDto;
import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.ownerService.interfaces.OwnerService;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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

    @Override
    public PublicViewRestaurantDto getPublicRestaurantDetailsUsingEncryptedId(String encryptedId) {

      //  Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);
        Long restaurantId = Long.parseLong(encryptedId);  // Temporarily till setting up encryptedIds
        log.info("Decrypted public request for restaurant ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

        PublicViewRestaurantDto dto = new PublicViewRestaurantDto();
        dto.setName(restaurant.getName());
        dto.setPhone(restaurant.getPhone());
        dto.setEncryptedId(idEncryptionService.encryptLongId(restaurant.getId()));

        return dto;

    }

    @Override
    public String getOwnerContactInfo(String encryptedRestaurantId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + restaurantId));

        return restaurant.getPhone();
    }


}
