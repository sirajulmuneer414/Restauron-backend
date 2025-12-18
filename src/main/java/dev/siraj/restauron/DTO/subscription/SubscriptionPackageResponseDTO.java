package dev.siraj.restauron.DTO.subscription;

import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;
import dev.siraj.restauron.entity.enums.subscription.PackageStatus;
import dev.siraj.restauron.entity.subscription.Offer;
import lombok.*;
import java.util.List;

    // Data Transfer Object for SubscriptionPackage response

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPackageResponseDTO {
    private Long id;
    private String name;
    private Integer durationAmount;
    private String durationType;
    private Double price;
    private Offer offer;
    private PackageStatus  status;
    private String description;
    private String createdAt;
    private String updatedAt;
    private int subscribedRestaurantsCount;
    private List<RestaurantSummaryDTO> subscribedRestaurants;
}
