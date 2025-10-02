package dev.siraj.restauron.DTO.restaurant;


import lombok.Data;

import java.util.List;

@Data
public class CategoryDto {
    private String name;
    private String description;
    private List<MenuItemDto> menuItems;
}