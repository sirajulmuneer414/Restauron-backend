package dev.siraj.restauron.service.restaurant.config;

import dev.siraj.restauron.DTO.restaurant.config.RestaurantConfigDTO;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.config.RestaurantConfig;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.repository.restaurantRepo.configuration.RestaurantConfigRepository;
import dev.siraj.restauron.service.cloudinaryService.ImageUploadService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

    // Implementation of RestaurantConfigService

@Service
public class RestaurantConfigServiceImp implements RestaurantConfigService {

    private static final String DEFAULT_PRIMARY_COLOR = "#f59e0b"; // Default Yellow
    private static final String DEFAULT_SECONDARY_COLOR = "#000000"; // Default Black


    private final RestaurantRepository restaurantRepository;
    private final RestaurantConfigRepository restaurantConfigRepository;
    private final IdEncryptionService idEncryptionService;
    private final ImageUploadService  imageUploadService;

    @Autowired
    public RestaurantConfigServiceImp(RestaurantRepository restaurantRepository,
                                     RestaurantConfigRepository restaurantConfigRepository,
                                     IdEncryptionService idEncryptionService,
                                     ImageUploadService imageUploadService) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantConfigRepository = restaurantConfigRepository;
        this.idEncryptionService = idEncryptionService;
        this.imageUploadService = imageUploadService;
    }



    @Override
    public RestaurantConfigDTO getConfigByEncryptedId(String encryptedId) {

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedId);

        return restaurantConfigRepository.findByRestaurant_Id(restaurantId)
                .map(this::mapToDTO)
                .orElseGet(() -> {
                    Restaurant r = restaurantRepository.findById(restaurantId)
                            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
                    return createDefaultConfigDTO(r);
                });
    }

    @Override
    public RestaurantConfigDTO updateRestaurantConfig(String encryptedRestaurantId, RestaurantConfigDTO dto, MultipartFile bannerImage) {

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);


        // 1. Find the Restaurant using the ID from the Header
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with ID: " + encryptedRestaurantId));

        // 2. Find existing config or create a new one attached to this restaurant
        RestaurantConfig config = restaurantConfigRepository.findByRestaurant(restaurant)
                .orElse(RestaurantConfig.builder()
                        .restaurant(restaurant)
                        // Set reasonable defaults for a new config
                        .primaryColor(DEFAULT_PRIMARY_COLOR)
                        .secondaryColor(DEFAULT_SECONDARY_COLOR)
                        .buttonTextColor(DEFAULT_SECONDARY_COLOR)
                        .build());

        // 3. Upload Image if provided (and not empty)
        if (bannerImage != null && !bannerImage.isEmpty()) {
            // Optional: Delete old image to keep Cloudinary clean
            if (config.getBannerUrl() != null && !config.getBannerUrl().isEmpty()) {
                imageUploadService.deleteImageByUrl(config.getBannerUrl());
            }
            // Upload new one
            String url = imageUploadService.imageUploader(bannerImage, "restauron/banners");
            config.setBannerUrl(url);
        }

        // 4. Update Fields from DTO
        // We check for nulls to allow partial updates if needed,
        // though typically the form sends all values.
        if (dto.getPrimaryColor() != null) config.setPrimaryColor(dto.getPrimaryColor());
        if (dto.getSecondaryColor() != null) config.setSecondaryColor(dto.getSecondaryColor());
        if (dto.getButtonTextColor() != null) config.setButtonTextColor(dto.getButtonTextColor());

        config.setCenterQuote(dto.getCenterQuote());
        config.setTopLeftQuote(dto.getTopLeftQuote());
        config.setBestFeature(dto.getBestFeature());
        config.setLocationText(dto.getLocationText());
        config.setOpeningTime(dto.getOpeningTime());
        config.setClosingTime(dto.getClosingTime());

        // Booleans can just be set directly
        config.setManualOpen(dto.isOpenManual());
        config.setUseManualOpen(dto.isUseManualOpen());

        // 5. Save and Return
        RestaurantConfig saved = restaurantConfigRepository.save(config);
        return mapToDTO(saved);
    }



    // --------------------------------------- HELPER METHODS --------------------------------------- //

    private RestaurantConfigDTO mapToDTO(RestaurantConfig config) {
        RestaurantConfigDTO dto = new RestaurantConfigDTO();
        dto.setRestaurantName(config.getRestaurant().getName());
        dto.setBannerUrl(config.getBannerUrl());
        dto.setPrimaryColor(config.getPrimaryColor());
        dto.setSecondaryColor(config.getSecondaryColor());
        dto.setButtonTextColor(config.getButtonTextColor());
        dto.setCenterQuote(config.getCenterQuote());
        dto.setTopLeftQuote(config.getTopLeftQuote());
        dto.setBestFeature(config.getBestFeature());
        dto.setLocationText(config.getLocationText());
        dto.setOpeningTime(config.getOpeningTime());
        dto.setClosingTime(config.getClosingTime());
        dto.setOpenManual(config.isManualOpen());
        dto.setUseManualOpen(config.isUseManualOpen());
        return dto;
    }

    private RestaurantConfigDTO createDefaultConfigDTO(Restaurant r) {
        RestaurantConfigDTO dto = new RestaurantConfigDTO();
        dto.setRestaurantName(r.getName());
        dto.setPrimaryColor(DEFAULT_PRIMARY_COLOR); // Default Yellow
        dto.setSecondaryColor(DEFAULT_SECONDARY_COLOR); // Default Black
        dto.setButtonTextColor(DEFAULT_SECONDARY_COLOR); // Default Black
        return dto;
    }
}

