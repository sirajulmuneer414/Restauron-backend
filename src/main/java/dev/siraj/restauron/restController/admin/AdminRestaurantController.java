package dev.siraj.restauron.restController.admin;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/admin/restaurant")
@RolesAllowed(roles = {"ADMIN"})
@Slf4j
public class AdminRestaurantController {

    @Autowired
    private RestaurantInitialService restaurantInitialService;

    @Autowired
    private AdminService adminService;



    // This method is to fetch all restaurant registration requests ( Filters and Pagination Applicable )
    @PostMapping("/approval-requests")
    public ResponseEntity<Page<RestaurantInitialResponse>> getAllRestaurantRequests(@RequestBody PageRequestDto pageDetails){

        log.info("In the API for getting restaurant Details");

        Page<RestaurantInitialResponse> responsePage = restaurantInitialService.findAllRestaurantInitialsByPageRequestDtoMappedToInitialResponse(pageDetails);


        if(responsePage.isEmpty()){
            return ResponseEntity.status(500).body(null);
        }

        return new ResponseEntity<>(responsePage, HttpStatus.OK);

    }

    // Inorder to fetch individual request details
    @GetMapping("/request/{restaurantRegistrationId}")
    public ResponseEntity<?> getRestaurantDetails(@PathVariable Long restaurantRegistrationId){

        log.info("In the API for getting individual restaurant registration details");

        if(restaurantInitialService.restaurantRegistrationExistsById(restaurantRegistrationId)){

            RestaurantRegistration restaurantRegistration = restaurantInitialService.findRestaurantRegistrationById(restaurantRegistrationId);

            return new ResponseEntity<>(restaurantRegistration, HttpStatus.OK);

        }

        return new ResponseEntity<>("Restaurant not found", HttpStatus.BAD_REQUEST);

    }




    // Method to update the AccountStatus to REJECTED, PENDING or VERIFIED
    @PostMapping("/status-update")
    public ResponseEntity<?> setRestaurantStatus(@RequestParam("restaurantId") Long restaurantId,
                                                 @RequestParam("statusUpdateTo") String StatusUpdateTo){


        log.info("Reached the controller for updating restaurant registration status");

        boolean isUpdated = adminService.updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(restaurantId,StatusUpdateTo);

        return new ResponseEntity<>(isUpdated, HttpStatus.OK);

    }
}
