package dev.siraj.restauron.restController.admin;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
        RestController class for dealing with
        APIs regarding restaurant from Admin Side
 */

@RestController
@RequestMapping("/api/admin/restaurant")
@RolesAllowed(roles = {"ADMIN"})
@Slf4j
public class AdminRestaurantRegistrationController {


    private final RestaurantInitialService restaurantInitialService;


    private final AdminService adminService;

    public AdminRestaurantRegistrationController(RestaurantInitialService restaurantInitialService, AdminService adminService) {
        this.restaurantInitialService = restaurantInitialService;
        this.adminService = adminService;
    }



    // This method is to fetch all restaurant registration requests ( Filters and Pagination Applicable )
    @PostMapping("/approval-requests")
    public ResponseEntity<Page<RestaurantInitialResponse>> getAllRestaurantRequests(@RequestBody PageRequestDto pageDetails){

        log.info("In the API for getting restaurant Details");

        Page<RestaurantInitialResponse> responsePage = restaurantInitialService.findAllRestaurantInitialsByPageRequestDtoMappedToInitialResponse(pageDetails);


        return new ResponseEntity<>(responsePage, HttpStatus.OK);

    }

    // Inorder to fetch individual request details
    @GetMapping("/request/{restaurantRegistrationId}")
    public ResponseEntity<RestaurantRegistration> getRestaurantDetails(@PathVariable Long restaurantRegistrationId){

        log.info("In the API for getting individual restaurant registration details");

        if(restaurantInitialService.restaurantRegistrationExistsById(restaurantRegistrationId)){

            RestaurantRegistration restaurantRegistration = restaurantInitialService.findRestaurantRegistrationById(restaurantRegistrationId);

            return new ResponseEntity<>(restaurantRegistration, HttpStatus.OK);

        }

        else {

            log.error("No restaurant registration found with id : {}", restaurantRegistrationId);
            throw new EntityNotFoundException("Restaurant Registration not found");

        }

    }




    // Method to update the AccountStatus to REJECTED, PENDING or VERIFIED
    @PostMapping("/status-update")
    public ResponseEntity<Boolean> setRestaurantStatus(@RequestParam("restaurantId") Long restaurantId,
                                                 @RequestParam("statusUpdateTo") String StatusUpdateTo){


        log.info("Reached the controller for updating restaurant registration status");

        boolean isUpdated = adminService.updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(restaurantId,StatusUpdateTo);

        return new ResponseEntity<>(isUpdated, HttpStatus.OK);

    }
}
