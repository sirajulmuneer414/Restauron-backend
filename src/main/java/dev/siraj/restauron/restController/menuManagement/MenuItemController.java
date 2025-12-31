package dev.siraj.restauron.restController.menuManagement;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemResponseDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.menuManagement.menuItemService.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RolesAllowed(roles = {"OWNER"})
@RequestMapping("/api/owner/menu")
@Slf4j
public class MenuItemController {

        private final MenuItemService menuItemService;

        @Autowired
        public MenuItemController(MenuItemService menuItemService) {
            this.menuItemService = menuItemService;
        }

        /**
         * Creates a new menu item.
         */
        @PostMapping("/create")
        public ResponseEntity<String> createMenuItem(
                @RequestParam("name") String name,
                @RequestParam("description") String description,
                @RequestParam("price") Double price,
                @RequestParam("isVegetarian") Boolean isVegetarian,
                @RequestParam("categoryEncryptedId") String categoryEncryptedId,
                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, // Optional
                @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId
        ) {
            // Build DTO or pass params to service



            MenuItemRequestDto dto = new MenuItemRequestDto();
            dto.setName(name);
            dto.setDescription(description);
            dto.setPrice(price);
            dto.setIsVegetarian(isVegetarian);
            dto.setCategoryEncryptedId(categoryEncryptedId);
            dto.setImageFile(imageFile);

            menuItemService.createMenuItem(dto, restaurantEncryptedId);

            return new ResponseEntity<>("Menu item created successfully.", HttpStatus.CREATED);
        }

    /**
         * Fetches a single menu item by its encrypted ID.
         */
        @GetMapping("/fetch/{menuItemEncryptedId}")
        public ResponseEntity<MenuItemResponseDto> fetchMenuItem(@PathVariable String menuItemEncryptedId) {
            log.info("Request to fetch menu item with ID: {}", menuItemEncryptedId);
            MenuItemResponseDto dto = menuItemService.findMenuItemByEncryptedId(menuItemEncryptedId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }

        /**
         * Fetches a paginated list of menu items for the entire restaurant, with optional search.
         */
        @PostMapping("/fetch-by-restaurant")
        public ResponseEntity<Page<MenuItemResponseDto>> fetchMenuItemsByRestaurant(@RequestBody PageRequestDto pageRequest, @RequestHeader("X-Restaurant-Id") String restaurantEncryptedId) {
            log.info("Request to fetch menu items for restaurant.");
            Page<MenuItemResponseDto> page = menuItemService.findMenuItemsByRestaurant(
                    restaurantEncryptedId, pageRequest.getSearch(), pageRequest.getPageNo(), pageRequest.getSize());
            log.info("Fetched page of menu items by restaurant");
            return new ResponseEntity<>(page, HttpStatus.OK);
        }

        /**
         * Fetches a paginated list of menu items for a specific category.
         * Note: This now accepts a PageRequestDto in the body to allow for future pagination/search within a category.
         */
        @PostMapping("/fetch-by-category/{categoryEncryptedId}")
        public ResponseEntity<Page<MenuItemResponseDto>> fetchMenuItemsByCategory(@PathVariable String categoryEncryptedId, @RequestBody PageRequestDto pageRequestDto) {
            log.info("Request to fetch menu items for category ID: {}", categoryEncryptedId);
            Page<MenuItemResponseDto> page = menuItemService.findMenuItemsByCategory(categoryEncryptedId, pageRequestDto);
            log.info("Fetched page of menu items by categories");
            return new ResponseEntity<>(page, HttpStatus.OK);
        }

        /**
         * Updates the details of an existing menu item.
         */
        @PutMapping("/update/{menuItemEncryptedId}")
        public ResponseEntity<String> updateMenuItem(@PathVariable String menuItemEncryptedId, @RequestBody MenuItemRequestDto request) {
            log.info("Request to update menu item ID: {}  {}  {}", menuItemEncryptedId, request.getName(), request.getIsVegetarian());
            menuItemService.updateMenuItem(menuItemEncryptedId, request);
            log.info("updated menu item successfully");
            return new ResponseEntity<>("Menu item updated successfully.", HttpStatus.OK);
        }

        /**
         * Toggles the availability of a menu item.
         */
        @PatchMapping("/update-availability/{menuItemEncryptedId}")
        public ResponseEntity<String> updateMenuItemAvailability(@PathVariable String menuItemEncryptedId, @RequestBody Map<String, Boolean> payload) {
            Boolean isAvailable = payload.get("isAvailable");
            if (isAvailable == null) {
                return new ResponseEntity<>("'isAvailable' field is required in the payload.", HttpStatus.BAD_REQUEST);
            }
            log.info("Request to update availability for menu item ID: {} to {}", menuItemEncryptedId, isAvailable);
            menuItemService.updateMenuItemAvailability(menuItemEncryptedId, isAvailable);
            log.info("updated availability of menu item");
            return new ResponseEntity<>("Availability updated successfully.", HttpStatus.OK);
        }

        /**
         * Deletes a menu item.
         */
        @DeleteMapping("/delete/{menuItemEncryptedId}")
        public ResponseEntity<Void> deleteMenuItem(@PathVariable String menuItemEncryptedId) {
            log.info("Request to delete menu item ID: {}", menuItemEncryptedId);
            menuItemService.deleteMenuItem(menuItemEncryptedId);
            log.info("deleted menu item successfully");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }