package dev.siraj.restauron.service.employee.employeeMenuService;

import dev.siraj.restauron.DTO.restaurant.CategoryDto;
import dev.siraj.restauron.DTO.restaurant.MenuItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

    // This is interface for Employee Menu Service
public interface EmployeeMenuService {
        List<MenuItemDto> getMenuForPOS(String encryptedRestaurantId);

        Page<MenuItemDto> searchMenu(String encryptedRestaurantId, String search, String category, Pageable pageable);

        List<CategoryDto> getMenuCategories(String encryptedRestaurantId);
    }
