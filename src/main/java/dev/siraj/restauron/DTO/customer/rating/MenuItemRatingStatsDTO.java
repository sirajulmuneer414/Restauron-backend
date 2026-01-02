package dev.siraj.restauron.DTO.customer.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRatingStatsDTO {
    private Double averageRating;
    private Long totalRatings;
}