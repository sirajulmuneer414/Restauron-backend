package dev.siraj.restauron.service.menuManagement.categoryService;

import dev.siraj.restauron.DTO.owner.menuManagement.CategoryResponseDto;
import dev.siraj.restauron.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface CategoryService {
    void createCategory(Map<String, String> payload, String restaurantEncryptedId) throws Exception;

    CategoryResponseDto findCategoryByEncryptedId(String categoryEncryptedId);

    Page<CategoryResponseDto> findCategoriesByRestaurantId(String restaurantEncryptedId, String name, int page, int size);

    void updateCategory(String categoryEncryptedId, String newName, String newDescription);

    void updateCategoryStatus(String categoryEncryptedId, ItemStatus status);

    void deleteCategory(String categoryEncryptedId) throws Exception;
}
