package dev.siraj.restauron.service.menuManagement.menuItemService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemRequestDto;
import dev.siraj.restauron.DTO.owner.menuManagement.MenuItemResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuItemService {

    void createMenuItem(MenuItemRequestDto request, String restaurantEncryptedId);

    MenuItemResponseDto findMenuItemByEncryptedId(String menuItemEncryptedId);

    Page<MenuItemResponseDto> findMenuItemsByRestaurant(String restaurantEncryptedId, String name, int page, int size);

    Page<MenuItemResponseDto> findMenuItemsByCategory(String categoryEncryptedId, PageRequestDto pageRequestDto);

    void updateMenuItem(String menuItemEncryptedId, MenuItemRequestDto request);

    void updateMenuItemAvailability(String menuItemEncryptedId, boolean isAvailable);

    void deleteMenuItem(String menuItemEncryptedId);

}
