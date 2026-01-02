package dev.siraj.restauron.DTO.owner.customerSideUrl;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantLinkDTO {
    private String customerPageUrl;
    private String encryptedRestaurantId;
    private String restaurantName;
}