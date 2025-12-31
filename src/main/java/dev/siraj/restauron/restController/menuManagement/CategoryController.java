package dev.siraj.restauron.restController.menuManagement;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.CategoryResponseDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.enums.AvailabilityStatus;
import dev.siraj.restauron.service.menuManagement.categoryService.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/owner/category")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCategoryForMenu(@RequestBody Map<String, String> payload, @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId) throws Exception {
        log.info("Request to create category: {}", payload.get("name"));
        categoryService.createCategory(payload, restaurantEncryptedId);
        return new ResponseEntity<>("Category created successfully.", HttpStatus.CREATED);
    }

    @GetMapping("/fetch/{categoryEncryptedId}")
    public ResponseEntity<CategoryResponseDto> getIndividualCategory(@PathVariable String categoryEncryptedId){
        log.info("Inside the controller to fetch individual category details of : {}",categoryEncryptedId);

        CategoryResponseDto dto = categoryService.findCategoryByEncryptedId(categoryEncryptedId);

        log.info("Completed Fetching category details");
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    @PostMapping("/fetch-list")
    public ResponseEntity<Page<CategoryResponseDto>> fetchCategoriesForRestaurant(@RequestBody PageRequestDto pageRequestDto, @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId) {
        log.info("Request to fetch category list for restaurant.");
        Page<CategoryResponseDto> page = categoryService.findCategoriesByRestaurantId(
                restaurantEncryptedId,
                pageRequestDto.getSearch(),
                pageRequestDto.getPageNo(),
                pageRequestDto.getSize()
        );
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * Endpoint to update a category's name and/or description.
     */
    @PutMapping("/update/{categoryEncryptedId}")
    public ResponseEntity<String> updateCategory(
            @PathVariable String categoryEncryptedId,
            @RequestBody Map<String, String> payload) {

        String newName = payload.get("name");
        String newDescription = payload.get("description");

        log.info("Request to update category ID: {}", categoryEncryptedId);

        if (!StringUtils.hasText(newName) && newDescription == null) {
            return new ResponseEntity<>("At least 'name' or 'description' must be provided.", HttpStatus.BAD_REQUEST);
        }

        categoryService.updateCategory(categoryEncryptedId, newName, newDescription);

        return new ResponseEntity<>("Category updated successfully.", HttpStatus.OK);
    }

    /**
     * Endpoint to update ONLY the status of a category.
     * Uses PATCH as it's a partial update.
     */
    @PatchMapping("/update-status/{categoryEncryptedId}")
    public ResponseEntity<String> updateCategoryStatus(
            @PathVariable String categoryEncryptedId,
            @RequestBody Map<String, String> payload) {

        String statusStr = payload.get("status");
        log.info("Request to update status for category ID: {} to {}", categoryEncryptedId, statusStr);

        AvailabilityStatus newStatus;
        try {
            newStatus = AvailabilityStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return new ResponseEntity<>("Invalid status provided. Must be one of: AVAILABLE, UNAVAILABLE, LOW, RESTOCKING.", HttpStatus.BAD_REQUEST);
        }

        categoryService.updateCategoryStatus(categoryEncryptedId, newStatus);

        return new ResponseEntity<>("Category status updated successfully.", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{categoryEncryptedId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryEncryptedId) throws Exception {
        log.info("Request to delete category ID: {}", categoryEncryptedId);
        categoryService.deleteCategory(categoryEncryptedId);
        log.info("Deleted the category");

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
