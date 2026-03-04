package dev.siraj.restauron.DTO.publicApi.tableOrder;

/**
 * Public DTO for menu items available for table-side ordering
 * Contains only customer-facing information
 */
public record MenuItemPublicDTO(
        String encryptedId,
        String name,
        String description,
        Double price,
        String imageUrl,
        boolean isVegetarian,
        String categoryName) {
}
