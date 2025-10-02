package dev.siraj.restauron.DTO.restaurant;

import lombok.Data;

import java.util.List;

@Data
public class MenuDto {
    private String restaurantEncryptedId;
    private String restaurantName;
    private List<CategoryDto> categories;
}