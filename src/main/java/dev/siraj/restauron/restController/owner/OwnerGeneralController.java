package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    // REST controller for general owner-related endpoints

@RestController
@RequestMapping("/api/owner")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class OwnerGeneralController {

    private final RestaurantService restaurantService;

    @Autowired
    public OwnerGeneralController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Endpoint to get restaurant details for the owner after login.
     *
     * @param ownerUserId Encrypted owner user ID from path variable
     * @return ResponseEntity containing RestaurantReduxSettingDto
     */
    @GetMapping("/get-restaurant-details/{ownerUserId}")
    public ResponseEntity<?> getRestaurantDetailsToSet(@PathVariable String ownerUserId){



        log.info("Inside the get restaurant details after login {}",ownerUserId);
        RestaurantReduxSettingDto dto = restaurantService.findRestaurantByOwnerFromEncryptedId(ownerUserId);

        System.out.println(dto.getRestaurantEncryptedId() + " "+ dto.getRestaurantName());

        log.info("Finished fetching the restaurant details");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Endpoint to get the owner's contact information for the restaurant.
     *
     * @param encryptedRestaurantId Encrypted restaurant ID from request header
     * @return ResponseEntity containing owner's contact information
     */
    @GetMapping("/contact-info")
    public ResponseEntity<?> getOwnerContactInfo(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Fetching contact info for restaurant ID: {}", encryptedRestaurantId);
        String contactInfo = restaurantService.getOwnerContactInfo(encryptedRestaurantId);
        return new ResponseEntity<>(contactInfo, HttpStatus.OK);
    }


}
