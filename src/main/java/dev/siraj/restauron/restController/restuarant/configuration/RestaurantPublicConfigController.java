package dev.siraj.restauron.restController.restuarant.configuration;


import dev.siraj.restauron.DTO.restaurant.config.RestaurantConfigDTO;
import dev.siraj.restauron.service.restaurant.config.RestaurantConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/restaurant/config")
@Slf4j
public class RestaurantPublicConfigController {

    @Autowired
    private RestaurantConfigService restaurantConfigService;

    @GetMapping("/{encryptedId}")
    public ResponseEntity<RestaurantConfigDTO> getPublicRestaurantConfig(
            @PathVariable String encryptedId
    ) {

        log.info("Fetching public restaurant config for ID: {}", encryptedId);

        return ResponseEntity.ok(restaurantConfigService.getConfigByEncryptedId(encryptedId));
    }
}
