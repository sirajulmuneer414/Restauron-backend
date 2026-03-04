package dev.siraj.restauron.DTO.publicApi.tableOrder;

import dev.siraj.restauron.entity.enums.table.TableStatus;

import java.util.List;

/**
 * Response DTO containing table information and available menu items
 * for customer-facing table-side ordering
 */
public record TableOrderInfoResponseDTO(
        TableInfoDTO table,
        RestaurantInfoDTO restaurant,
        List<CategoryWithItemsDTO> menuCategories) {
    public record TableInfoDTO(
            String encryptedId,
            String name,
            Integer capacity,
            TableStatus status) {
    }

    public record RestaurantInfoDTO(
            String encryptedId,
            String name) {
    }
}
