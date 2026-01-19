package dev.siraj.restauron.DTO.owner.restaurantManagement;

import lombok.Data;

@Data
public class TableResponseDto {

    private String encryptedId;
    private Long tableId;
    private String name;
    private Integer capacity;
}
