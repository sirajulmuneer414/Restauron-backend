package dev.siraj.restauron.service.ownerService;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.ownerService.interfaces.OwnerService;
import jakarta.persistence.EntityNotFoundException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerServiceImp implements OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired private IdEncryptionService idEncryptionService;


    @Override
    public Owner findOwnerById(Long ownerId) {

        return ownerRepository.findById(ownerId).get();

    }

    @Override
    public Owner findOwnerByUser(UserAll user) {
        return ownerRepository.findByUser(user);
    }

    @Override
    public Long getOwnerIdFromRestaurantEncryptedId(String encryptedRestaurantId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("The restaurant is not found"));

        Long ownerId = restaurant.getOwner().getUser().getId();
        return ownerId;
    }
}
