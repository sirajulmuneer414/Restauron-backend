package dev.siraj.restauron.service.customer.profileServices;

import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerStatusDto;
import dev.siraj.restauron.DTO.customer.profileGeneral.CustomerUpdateRequest;
import dev.siraj.restauron.DTO.owner.customerManagement.CustomerResponseDto;
import dev.siraj.restauron.entity.users.Customer;

public interface CustomerProfileService {
    CustomerStatusDto getCustomerStatusForRestaurant(String userId, String restaurantEncryptedId);

    CustomerResponseDto getProfile(String encryptedCustomerId);

    void updateProfile(String encryptedCustomerId, CustomerUpdateRequest updateRequest);

    void deleteAccount(String encryptedCustomerId);
}
