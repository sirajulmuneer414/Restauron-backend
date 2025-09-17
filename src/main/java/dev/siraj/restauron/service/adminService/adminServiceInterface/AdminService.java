package dev.siraj.restauron.service.adminService.adminServiceInterface;

import dev.siraj.restauron.DTO.admin.UserListResponse;

public interface AdminService {
    boolean updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(Long restaurantId, String statusUpdateTo);

    UserListResponse getUserDetailsById(String encryptedId);
}
