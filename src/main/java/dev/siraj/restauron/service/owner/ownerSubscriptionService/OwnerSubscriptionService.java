package dev.siraj.restauron.service.owner.ownerSubscriptionService;

import dev.siraj.restauron.DTO.owner.subscription.OwnerSubscriptionHomeDTO;

public interface OwnerSubscriptionService {

    OwnerSubscriptionHomeDTO getSubscriptionHome(String restaurantEncryptedId);

}
