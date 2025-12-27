package dev.siraj.restauron.restController.general;

import dev.siraj.restauron.DTO.customer.PublicViewRestaurantDto;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@Slf4j
public class HomeController {

    @Autowired private RestaurantService restaurantService;

    @GetMapping("/restaurant/details/{encryptedId}")
    public ResponseEntity<PublicViewRestaurantDto> getRestaurantDetails(@PathVariable String encryptedId){

        log.info("Public request received for restaurant details with encrypted ID: {}", encryptedId);
        try {
            PublicViewRestaurantDto restaurantDetails = restaurantService.getPublicRestaurantDetailsUsingEncryptedId(encryptedId);
            return ResponseEntity.ok(restaurantDetails);
        } catch (EntityNotFoundException e) {
            log.warn("Public request failed. Could not find restaurant: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("An unexpected public API error occurred.", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
