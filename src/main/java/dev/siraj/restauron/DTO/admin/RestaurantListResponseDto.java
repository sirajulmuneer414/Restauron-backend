package dev.siraj.restauron.DTO.admin;

import lombok.Data;

@Data
public class RestaurantListResponseDto {
    private String encryptedId;
    private String name;
    private String email;
    private String phone;
    private String ownerName;
    private String status; // Assuming Restaurant has a status field (e.g., PENDING, APPROVED)
}