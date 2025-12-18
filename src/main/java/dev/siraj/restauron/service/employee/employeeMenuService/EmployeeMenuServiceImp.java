package dev.siraj.restauron.service.employee.employeeMenuService;

    // This is implementation for Employee Menu Service

import dev.siraj.restauron.DTO.restaurant.CategoryDto;
import dev.siraj.restauron.DTO.restaurant.MenuItemDto;
import dev.siraj.restauron.entity.menuManagement.Category;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import dev.siraj.restauron.respository.menuManagement.categoryRepo.CategoryRepository;
import dev.siraj.restauron.respository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

    // Implementation of Employee Menu Service

@Service
@Slf4j
public class EmployeeMenuServiceImp implements EmployeeMenuService{

    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private IdEncryptionService idEncryptionService;
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * This method retrieves the menu items for a given encrypted restaurant ID.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant.
     * @return A list of MenuItem entities.
     */
    @Override
    public List<MenuItemDto> getMenuForPOS(String encryptedRestaurantId) {

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        List<MenuItem> listMenu = menuItemRepository.findByRestaurantId(restaurantId);

        return listMenu.stream().map(this::mapToMenuItemDto).toList();
    }

    /**
     * This method searches for menu items based on the provided criteria.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant.
     * @param search                The search term to filter menu items.
     * @param category              The category filter for menu items.
     * @param pageable              The pagination information.
     * @return A paginated list of MenuItem entities matching the search criteria.
     */
    @Override
    public Page<MenuItemDto> searchMenu(String encryptedRestaurantId, String search, String category, Pageable pageable) {

        Specification<MenuItem> specification = buildSpecification(encryptedRestaurantId, search, category);

        Page<MenuItem> itemPage =  menuItemRepository.findAll(specification, pageable);

        return itemPage.map(this::mapToMenuItemDto);
    }


    @Override
    public List<CategoryDto> getMenuCategories(String encryptedRestaurantId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        List<Category> categoryList = categoryRepository.findByRestaurantId(restaurantId);
        return categoryList.stream().map(this::mapToCategoryDto).toList();
    }


    // ---------------------------------------------- HELPER METHODS -----------------------------------------------------------------------------


    private Specification<MenuItem> buildSpecification(String encryptedRestaurantId, String search, String category) {

        //Decrypting restaurant ID
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        return (root, query, criteriaBuilder) -> {
            // Root predicate - always filter by the owner's restaurant
            Predicate finalPredicate = criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId);

            // Adding status filter if provided and not "ALL"
            if(StringUtils.hasText(category) && !category.equalsIgnoreCase("ALL")){
                try{

                    Long categoryId = idEncryptionService.decryptToLongId(category);

                    // Join with userAll to access status
                    finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(root.get("category").get("id"), categoryId));

                }catch (IllegalArgumentException e){
                    log.warn("Invalid status filter provided: {}", category);

                }
            }

            // adding searching filter if provided
            if(StringUtils.hasText(search)){

                String searchPattern = "%"+search.toLowerCase()+"%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("price")),searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),searchPattern)
                );

                finalPredicate = criteriaBuilder.and(finalPredicate, searchPredicate);
            }

            return finalPredicate;
        };
    }

    private MenuItemDto mapToMenuItemDto(MenuItem item) {
        MenuItemDto dto = new MenuItemDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(item.getId()));
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setImageUrl(item.getImageUrl());
        dto.setVeg(item.isVegetarian());
        dto.setAvailable(item.isAvailable());
        return dto;
    }


    private CategoryDto mapToCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setEncryptedCategoryId(idEncryptionService.encryptLongId(category.getId()));
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
