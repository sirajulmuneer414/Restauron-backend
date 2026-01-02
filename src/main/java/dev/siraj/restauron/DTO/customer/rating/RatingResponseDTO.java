package dev.siraj.restauron.DTO.customer.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    private String encryptedRatingId;
    private String menuItemName;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}