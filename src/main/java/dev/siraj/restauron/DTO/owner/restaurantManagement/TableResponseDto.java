package dev.siraj.restauron.DTO.owner.restaurantManagement;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import lombok.Data;

@Data
public class TableResponseDto {

    private String encryptedId;
    private String name;
}
