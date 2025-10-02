package dev.siraj.restauron.DTO.customer;

import lombok.Data;

@Data
public class PublicViewRestaurantDto {
    private String name;
    private String phone;
    private String profileLogo;
    private String encryptedId;
    // Add any other public-facing details like contact info, etc.
}
