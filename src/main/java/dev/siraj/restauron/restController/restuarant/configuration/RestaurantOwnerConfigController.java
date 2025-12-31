package dev.siraj.restauron.restController.restuarant.configuration;

import dev.siraj.restauron.DTO.restaurant.config.RestaurantConfigDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.restaurant.config.RestaurantConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Controller for Restaurant Owner Configuration Endpoints

@RestController
@RequestMapping("/api/owner/restaurant/config")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class RestaurantOwnerConfigController {

    private final RestaurantConfigService restaurantConfigService;

    @Autowired
    public RestaurantOwnerConfigController(RestaurantConfigService restaurantConfigService) {
        this.restaurantConfigService = restaurantConfigService;
    }

    /**
     * Endpoint to get the restaurant configuration for the owner.
     *
     * @param encryptedRestaurantId Encrypted ID of the restaurant from request header.
     * @return ResponseEntity containing RestaurantConfigDTO.
     */
    @GetMapping()
    public ResponseEntity<RestaurantConfigDTO> getOwnerRestaurantConfig(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ) {

        log.info("Fetching owner restaurant config for ID: {}", encryptedRestaurantId);

        RestaurantConfigDTO configDTO = restaurantConfigService.getConfigByEncryptedId(encryptedRestaurantId);

        return ResponseEntity.ok(configDTO);
    }


    @PutMapping( consumes = {"multipart/form-data"})
    public ResponseEntity<RestaurantConfigDTO> updateOwnerRestaurantConfig(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
            @ModelAttribute RestaurantConfigDTO restaurantConfigDTO
    ) {

        log.info("Updating owner restaurant config for ID: {}", encryptedRestaurantId);

        return ResponseEntity.ok(restaurantConfigService.updateRestaurantConfig(encryptedRestaurantId, restaurantConfigDTO, bannerImage));

    }
}
