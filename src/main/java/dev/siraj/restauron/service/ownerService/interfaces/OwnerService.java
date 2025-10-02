package dev.siraj.restauron.service.ownerService.interfaces;

import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;

public interface OwnerService {
    Owner findOwnerById(Long ownerId);

    Owner findOwnerByUser(UserAll user);

    Long getOwnerIdFromRestaurantEncryptedId(String encryptedRestaurantId);
}
