package dev.siraj.restauron.DTO.owner.menuManagement;

import lombok.Data;

@Data
public class CategoryResponseDto {
    private String encryptedId;

    private String name;

    private String menuItemsPresent;

    private String description;

    private String status;
}
