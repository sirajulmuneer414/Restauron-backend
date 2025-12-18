package dev.siraj.restauron.restController.employee.menu;


import dev.siraj.restauron.DTO.restaurant.CategoryDto;
import dev.siraj.restauron.DTO.restaurant.MenuItemDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employee.employeeMenuService.EmployeeMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for Employee Menu related endpoints

@RestController
@RequestMapping("/employee/menu")
@RolesAllowed(roles = {"EMPLOYEE"})
public class EmployeeMenuController {

    @Autowired
    private EmployeeMenuService employeeMenuService;


    /**
     * Endpoint to retrieve the menu for a restaurant.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant from request header.
     * @return ResponseEntity containing a list of MenuItem entities.
     */
    @GetMapping()
    public ResponseEntity<List<MenuItemDto>> getMenu(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ) {
        return ResponseEntity.ok(employeeMenuService.getMenuForPOS(encryptedRestaurantId));

    }


    @GetMapping("/search")
    public ResponseEntity<Page<MenuItemDto>> searchMenu(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "All") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(employeeMenuService.searchMenu(encryptedRestaurantId, search, category, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getMenuCategories(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ) {
        return ResponseEntity.ok(employeeMenuService.getMenuCategories(encryptedRestaurantId));
    }
}
