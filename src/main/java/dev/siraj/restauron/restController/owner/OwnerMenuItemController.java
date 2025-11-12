package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemResponseDto;
import dev.siraj.restauron.DTO.restaurant.MenuItemDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.menuManagement.menuItemService.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner/menu-items")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class OwnerMenuItemController {

    @Autowired private MenuItemService menuItemService;

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemResponseDto>> getMenuItemsThroughSearch(@RequestHeader("X-Restaurant-Id") String restaurantEncryptedId,
                                                                               @RequestParam("name") String menuItemName
    ){

        log.info("Inside the controller for fetching menuItem list for owner {} for name containing {}", restaurantEncryptedId, menuItemName);
        List<MenuItemResponseDto> dtoList = menuItemService.getMenuItemListThroughNameSearch(restaurantEncryptedId, menuItemName);

        dtoList.forEach(System.out::println);

        log.info("Successfully fetched the required menuItem list");

        return ResponseEntity.ok(dtoList);

    }
}
