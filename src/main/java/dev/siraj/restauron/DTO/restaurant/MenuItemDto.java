package dev.siraj.restauron.DTO.restaurant;

import lombok.Data;

@Data
public class MenuItemDto {
    private String encryptedId;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private boolean isVeg;
    private boolean isAvailable;
}
