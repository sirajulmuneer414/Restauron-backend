package dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.registration.RestaurantRegistrationDto;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import org.springframework.data.domain.Page;

public interface RestaurantInitialService {

    RestaurantRegistration registerRestaurantForApproval(RestaurantRegistrationDto restaurantRegistrationDto);

    Long findRestaurantRegisterationOwnerIdByEmail(String email);

    void otpVerificationSuccessfulEnumChange(Long restaurantOwnerId);

    RestaurantRegistration findByUserEmail(String email);

    boolean registrationEmailExists(String email);

    Page<RestaurantInitialResponse> findAllRestaurantInitialsByPageRequestDtoMappedToInitialResponse(PageRequestDto pageDetails);

    RestaurantRegistration findRestaurantRegistrationById(Long restaurantId);

    void deleteRestaurantRegistrationOnApproval(RestaurantRegistration restaurantRegistration);

    boolean userExistsByEmail(String email);

    boolean restaurantRegistrationExistsById(Long restaurantRegistrationId);

    void rejectedByAdmin(RestaurantRegistration restaurantRegistration);
}
