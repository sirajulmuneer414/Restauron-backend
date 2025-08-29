package dev.siraj.restauron.service.adminService.adminServiceInterface;

public interface AdminService {
    boolean updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(Long restaurantId, String statusUpdateTo);
}
