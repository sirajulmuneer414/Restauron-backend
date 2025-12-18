package dev.siraj.restauron.DTO.owner.dashboard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDashboardSubscriptionDTO {
    private String planName;
    private String status; // "ACTIVE", "EXPIRED", "NONE"
    private Long daysLeft;
    private LocalDate expiryDate;
}