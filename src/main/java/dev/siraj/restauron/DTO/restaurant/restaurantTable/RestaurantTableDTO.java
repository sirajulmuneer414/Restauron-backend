package dev.siraj.restauron.DTO.restaurant.restaurantTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantTableDTO {
    private Long tableId;
    private String name;
    private String status;
    private Integer capacity;
}
