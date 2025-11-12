package dev.siraj.restauron.DTO.owner.menuManagement;

import lombok.Data;

@Data
public class MenuItemResponseDto {
    private String encryptedId;
    private Long menuItemId;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private boolean isVegetarian;
    private boolean isAvailable;
    private String status;
    private String categoryName;
}