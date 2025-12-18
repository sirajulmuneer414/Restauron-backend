package dev.siraj.restauron.DTO.admin.stats;

import lombok.Data;

    // Data Transfer Object for summarizing restaurant information in admin statistics

@Data
public class RestaurantSummaryDTO {
    private String createdAt;
    private String name;
    private String status;
    private long customerCount;
    private String restaurantEncryptedId;
}
