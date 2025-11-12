package dev.siraj.restauron.service.menuManagement.categoryService;

import dev.siraj.restauron.DTO.owner.menuManagement.CategoryResponseDto;
import dev.siraj.restauron.entity.enums.AvailabilityStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.respository.menuManagement.categoryRepo.CategoryRepository;
import dev.siraj.restauron.respository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;


@Service
@Slf4j
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     *
     * @param payload the payload consist of name and description if there is any
     * @param restaurantEncryptedId encrypted restaurant ID
     */
    @Override
    @Transactional
    public void createCategory(Map<String, String> payload, String restaurantEncryptedId) {
        Restaurant restaurant = restaurantRepository.findById(idEncryptionService.decryptToLongId(restaurantEncryptedId))
                .orElseThrow(() -> new EntityNotFoundException("The restaurant mentioned is not found for encrypted ID: " + restaurantEncryptedId));

        Category category = new Category();
        category.setName(payload.get("name"));
        category.setDescription(payload.getOrDefault("description", "No Description for this category"));
        category.setRestaurant(restaurant);
        // Set a default status upon creation
        category.setStatus(AvailabilityStatus.UNAVAILABLE); // As per your entity logic

        categoryRepository.save(category);
    }

    /**
     *
     * @param categoryEncryptedId encrypted Category ID
     * @return mapped to Category Response Dto
     */
    @Override
    public CategoryResponseDto findCategoryByEncryptedId(String categoryEncryptedId) {

        Category category = categoryRepository.findById(idEncryptionService.decryptToLongId(categoryEncryptedId)).orElseThrow(() -> new EntityNotFoundException("The category is not found for the encrypted id "+categoryEncryptedId));


        return mapToCategoryResponseDto(category);
    }

    /**
     *
     * @param restaurantEncryptedId encrypted restaurant id
     * @param name name to search for the category if there is any
     * @param page page no
     * @param size page size
     * @return page of category Response dtp
     */
    @Override
    public Page<CategoryResponseDto> findCategoriesByRestaurantId(String restaurantEncryptedId, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);

        Page<Category> pageFromDB;
        if (StringUtils.hasText(name)) {
            pageFromDB = categoryRepository.findByRestaurantIdAndNameContainingIgnoreCase(restaurantId, name, pageable);
        } else {
            pageFromDB = categoryRepository.findByRestaurantId(restaurantId, pageable);
        }

        return pageFromDB.map(this::mapToCategoryResponseDto);
    }

    private CategoryResponseDto mapToCategoryResponseDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(category.getId()));
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setStatus(category.getStatus().name()); // Include status in DTO
        dto.setMenuItemsPresent(Integer.toString(category.getAmountOfMenuItem()));
        return dto;
    }

    /**
     *
     * @param categoryEncryptedId encrypted category id
     * @param newName new Name to update
     * @param newDescription new description to update
     */

    @Override
    @Transactional
    public void updateCategory(String categoryEncryptedId, String newName, String newDescription) {
        Long categoryId = idEncryptionService.decryptToLongId(categoryEncryptedId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with encrypted id: " + categoryEncryptedId));

        if (StringUtils.hasText(newName)) {
            category.setName(newName);
        }
        if (newDescription != null) {
            category.setDescription(newDescription);
        }

        categoryRepository.save(category);
    }

    /**
     * Updates the status of a specific category.
     * @param categoryEncryptedId The encrypted ID of the category.
     * @param status The new status to set (e.g., "AVAILABLE", "UNAVAILABLE").
     */
    @Override
    @Transactional
    public void updateCategoryStatus(String categoryEncryptedId, AvailabilityStatus status) {
        Long categoryId = idEncryptionService.decryptToLongId(categoryEncryptedId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with encrypted id: " + categoryEncryptedId));

        category.setStatus(status);
        if(status.equals(AvailabilityStatus.UNAVAILABLE)){
            log.info("Category '{}' status set to UNAVAILABLE. Updating all associated menu items.", category.getName());
            int menuItemModifiedCount = menuItemRepository.updateStatusAndAvailabilityByCategoryId(categoryId, AvailabilityStatus.UNAVAILABLE,false);

            log.info("Bulk updated {} menu items for category ID {} to UNAVAILABLE and isAvailable=false.",menuItemModifiedCount , categoryId);

        }
        categoryRepository.save(category);
    }

    /**
     *
     * @param categoryEncryptedId encrypted category ID
     */
    @Override
    @Transactional
    public void deleteCategory(String categoryEncryptedId) {
        Long categoryId = idEncryptionService.decryptToLongId(categoryEncryptedId);
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Category not found with id: " + categoryEncryptedId);
        }
        categoryRepository.deleteById(categoryId);
    }
}