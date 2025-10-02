package dev.siraj.restauron.service.customer.profileServices;

import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerStatusDto;

public interface CustomerProfileService {
    CustomerStatusDto getCustomerStatusForRestaurant(String userId, String restaurantEncryptedId);
}
