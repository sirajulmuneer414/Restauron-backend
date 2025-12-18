package dev.siraj.restauron.service.restaurant.config;

    // Service interface for Restaurant Configuration

import dev.siraj.restauron.DTO.restaurant.config.RestaurantConfigDTO;
import org.springframework.web.multipart.MultipartFile;

public interface RestaurantConfigService {
    RestaurantConfigDTO getConfigByEncryptedId(String encryptedId);

    RestaurantConfigDTO updateRestaurantConfig(String encryptedRestaurantId, RestaurantConfigDTO restaurantConfigDTO, MultipartFile bannerImage);
}
