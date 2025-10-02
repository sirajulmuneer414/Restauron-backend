package dev.siraj.restauron.DTO.owner.menuManagement;

import lombok.Data;

@Data
public class MenuItemRequestDto {
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean isVegetarian;
    private String categoryEncryptedId; // Encrypted ID of the category it belongs to
}