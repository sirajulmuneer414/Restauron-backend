package dev.siraj.restauron.DTO.publicApi.tableOrder;

import java.util.List;

/**
 * DTO for a category with its available menu items
 */
public record CategoryWithItemsDTO(
        String encryptedId,
        String name,
        String description,
        List<MenuItemPublicDTO> items) {
}
