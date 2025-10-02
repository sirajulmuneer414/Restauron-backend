package dev.siraj.restauron.service.menuManagement.menuItemService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemResponseDto;
import dev.siraj.restauron.entity.enums.ItemStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.respository.menuManagement.categoryRepo.CategoryRepository;
import dev.siraj.restauron.respository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class MenuItemServiceImp implements MenuItemService{

    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private MenuItemRepository menuItemRepository;


    /**
     *
     * @param request
     * @param restaurantEncryptedId
     */
    @Override
    @Transactional
    public void createMenuItem(MenuItemRequestDto request, String restaurantEncryptedId) {
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found."));

        Long categoryId = idEncryptionService.decryptToLongId(request.getCategoryEncryptedId());
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found."));

        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setVegetarian(request.getIsVegetarian());
        menuItem.setCategory(category);
        menuItem.setRestaurant(restaurant);
        menuItem.setStatus(ItemStatus.AVAILABLE); // Default status
        menuItem.setAvailable(true);

        menuItemRepository.save(menuItem);

        category.addedMenuItem();
        category.setStatus(ItemStatus.AVAILABLE);

        categoryRepository.save(category);
    }

    /**
     *
     * @param menuItemEncryptedId
     * @return
     */
    @Override
    public MenuItemResponseDto findMenuItemByEncryptedId(String menuItemEncryptedId) {
        Long menuItemId = idEncryptionService.decryptToLongId(menuItemEncryptedId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu Item not found."));
        return mapToDto(menuItem);
    }

    private MenuItemResponseDto mapToDto(MenuItem menuItem) {
        MenuItemResponseDto dto = new MenuItemResponseDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(menuItem.getId()));
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setVegetarian(menuItem.isVegetarian());
        dto.setAvailable(menuItem.isAvailable());
        dto.setStatus(menuItem.getStatus().name());
        dto.setCategoryName(menuItem.getCategory().getName());
        return dto;
    }

    /**
     *
     * @param restaurantEncryptedId
     * @param name
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<MenuItemResponseDto> findMenuItemsByRestaurant(String restaurantEncryptedId, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);

        Page<MenuItem> pageFromDb;
        if (StringUtils.hasText(name)) {
            pageFromDb = menuItemRepository.findByRestaurantIdAndNameContainingIgnoreCase(restaurantId, name, pageable);
        } else {
            pageFromDb = menuItemRepository.findByRestaurantId(restaurantId, pageable);
        }

        return pageFromDb.map(this::mapToDto);
    }

    /**
     *
     * @param categoryEncryptedId
     * @param pageRequestDto
     * @return
     */
    @Override
    public Page<MenuItemResponseDto> findMenuItemsByCategory(String categoryEncryptedId, PageRequestDto pageRequestDto) {
        Long categoryId = idEncryptionService.decryptToLongId(categoryEncryptedId);
        int page = pageRequestDto.getPageNo();
        int size = pageRequestDto.getSize();
        String search = pageRequestDto.getSearch();

        // 2. Create a Pageable object for the query
        Pageable pageable = PageRequest.of(page, size);

        Page<MenuItem> pageFromDb;

        // 3. Conditionally call the correct repository method
        if (StringUtils.hasText(search)) {
            // If a search term is provided, use the filtering query
            pageFromDb = menuItemRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, search, pageable);
        } else {
            // Otherwise, use the query that only filters by category
            pageFromDb = menuItemRepository.findByCategoryId(categoryId, pageable);
        }

        // 4. Map the resulting Page<MenuItem> to a Page<MenuItemResponseDto>
        // This reuses your existing mapToDto method to keep the code DRY.
        return pageFromDb.map(this::mapToDto);
    }

    /**
     *
     * @param menuItemEncryptedId
     * @param request
     */
    @Override
    @Transactional
    public void updateMenuItem(String menuItemEncryptedId, MenuItemRequestDto request) {
        Long menuItemId = idEncryptionService.decryptToLongId(menuItemEncryptedId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found."));

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setVegetarian(request.getIsVegetarian());

        // If category needs to be changed
        Long categoryId = idEncryptionService.decryptToLongId(request.getCategoryEncryptedId());
        if (!menuItem.getCategory().getId().equals(categoryId)) {
            Category oldCategory = menuItem.getCategory();
            oldCategory.removedMenuItem();
            if(!menuItemRepository.existsByCategoryIdAndStatus(oldCategory.getId(), ItemStatus.AVAILABLE)){
                oldCategory.setStatus(ItemStatus.UNAVAILABLE);
                categoryRepository.save(oldCategory);
            }

            Category newCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("New category not found."));

            newCategory.addedMenuItem();
            newCategory.setStatus(ItemStatus.AVAILABLE);

            categoryRepository.save(newCategory);

            menuItem.setCategory(newCategory);
        }

        menuItemRepository.save(menuItem);
    }

    /**
     *
     * @param menuItemEncryptedId
     * @param isAvailable
     */
    @Override
    @Transactional
    public void updateMenuItemAvailability(String menuItemEncryptedId, boolean isAvailable) {
        Long menuItemId = idEncryptionService.decryptToLongId(menuItemEncryptedId);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found."));

        menuItem.setAvailable(isAvailable);
        Category category = menuItem.getCategory();

        if(isAvailable){
            menuItem.setStatus(ItemStatus.AVAILABLE);
        }else{
            menuItem.setStatus(ItemStatus.UNAVAILABLE);
        }

        menuItemRepository.save(menuItem);

        if(!menuItemRepository.existsByCategoryIdAndStatus(category.getId(), ItemStatus.AVAILABLE)){
            category.setStatus(ItemStatus.UNAVAILABLE);
        }

        categoryRepository.save(category);
    }

    /**
     *
     * @param menuItemEncryptedId
     */
    @Override
    @Transactional
    public void deleteMenuItem(String menuItemEncryptedId) {
        Long menuItemId = idEncryptionService.decryptToLongId(menuItemEncryptedId);

        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElseThrow(() -> new EntityNotFoundException("The MenuItem is not found"));

        Category category = menuItem.getCategory();

        menuItemRepository.delete(menuItem);

        category.removedMenuItem();

        if(!menuItemRepository.existsByCategoryIdAndStatus(category.getId(), ItemStatus.AVAILABLE)){
            category.setStatus(ItemStatus.UNAVAILABLE);
        }

        categoryRepository.save(category);
    }
}
