package dev.siraj.restauron.DTO.customer;

import lombok.Data;

@Data
public class PublicViewRestaurantDto {
    private String name;
    private String phone;
    private String profileLogo;
    // Add any other public-facing details like contact info, etc.
}
