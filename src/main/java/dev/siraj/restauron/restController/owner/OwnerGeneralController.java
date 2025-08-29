package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.restaurantService.restaurantServiceInterface.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owner")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class OwnerGeneralController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/get-restaurant-details/{ownerUserId}")
    public ResponseEntity<?> getRestaurantDetailsToSet(@PathVariable String ownerUserId){



        log.info("Inside the get restaurant details after login {}",ownerUserId);
        RestaurantReduxSettingDto dto = restaurantService.findRestaurantByOwnerFromEncryptedId(ownerUserId);

        System.out.println(dto.getRestaurantEncryptedId() + " "+ dto.getRestaurantName());

        log.info("Finished fetching the restaurant details");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
