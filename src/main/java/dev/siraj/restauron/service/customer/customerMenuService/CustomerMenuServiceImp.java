package dev.siraj.restauron.service.customer.customerMenuService;


import dev.siraj.restauron.DTO.restaurant.CategoryDto;
import dev.siraj.restauron.DTO.restaurant.MenuDto;
import dev.siraj.restauron.DTO.restaurant.MenuItemDto;
import dev.siraj.restauron.entity.enums.ItemStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.respository.menuManagement.categoryRepo.CategoryRepository;
import dev.siraj.restauron.respository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerMenuServiceImp implements CustomerMenuService {

    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private IdEncryptionService idEncryptionService;
    @Autowired private MenuItemRepository menuItemRepository;

    @Override
    public MenuDto getFullMenu(String restaurantEncryptedId) {
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found") );

        MenuDto menuDto = new MenuDto();
        menuDto.setRestaurantEncryptedId(restaurantEncryptedId);
        menuDto.setRestaurantName(restaurant.getName());

        // Fetch all active categories for the restaurant and map them
        menuDto.setCategories(categoryRepository.findByRestaurantIdAndStatus(restaurantId, ItemStatus.AVAILABLE)
                .stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toList()));

        return menuDto;
    }

    private CategoryDto mapToCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        List<MenuItem> menuItemsList = menuItemRepository.findByCategory(category).orElse(new ArrayList<>());
        // Filter and map active menu items within the category
        dto.setMenuItems(menuItemsList.stream()
                .filter(MenuItem::isAvailable)
                .map(this::mapToMenuItemDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private MenuItemDto mapToMenuItemDto(dev.siraj.restauron.entity.menuManagement.MenuItem item) {
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
}
