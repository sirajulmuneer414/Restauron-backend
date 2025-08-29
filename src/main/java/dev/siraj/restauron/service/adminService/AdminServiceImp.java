package dev.siraj.restauron.service.adminService;

import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminService;
import dev.siraj.restauron.service.registrarion.ownerRegistrationService.OwnerRegistrationService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import dev.siraj.restauron.service.registrarion.restaurantRegistrationService.RestaurantRegistrationService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImp implements AdminService {

    @Autowired
    private RestaurantInitialService restaurantInitialService;

    @Autowired
    private RestaurantRegistrationMapping restaurantRegistrationMapping;

    @Autowired
    private RestaurantRegistrationService restaurantRegistrationService;

    @Autowired
    private UserService userService;


    @Autowired
    private OwnerRegistrationService ownerRegistrationService;

    @Override
    @Transactional
    public boolean updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(Long restaurantId, String statusUpdateTo) {

        RestaurantRegistration restaurantRegistration = restaurantInitialService.findRestaurantRegistrationById(restaurantId);

        // WHEN THE STATUS IS BEING UPDATED INTO APPROVED

        if(statusUpdateTo.equals(PendingStatuses.APPROVED.name())){

            try {

                // register the userAll for authentication purpose - owner
                UserAll user = restaurantRegistrationMapping.restaurantRegistrationToUserForOwner(restaurantRegistration);

                // specified user information based on role for additional information - owner
                Owner owner = ownerRegistrationService.mapToOwnerAndSave(userService.saveUser(user), restaurantRegistration);

                // registering the restaurant from restaurantInitial
                Restaurant restaurant = restaurantRegistrationService.registerRestaurantByRestaurantRegistrationDetailsAndOwner(restaurantRegistration, owner);

                // deleting the restaurant initial after registering the restaurant and owner
                restaurantInitialService.deleteRestaurantRegistrationOnApproval(restaurantRegistration);
            } catch (Exception e) {
                return false;
            }

            return true;

        }

        // WHEN THE STATUS IS BEING UPDATED INTO REJECTED

        else if(statusUpdateTo.equals(PendingStatuses.REJECTED.name())){

            try {
                // method to set the restaurant registration status as rejected
                restaurantInitialService.rejectedByAdmin(restaurantRegistration);
            }catch (Exception e){
                return false;
            }

            return true;
        }


        return false;
    }
}
