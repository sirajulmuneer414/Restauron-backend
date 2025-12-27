package dev.siraj.restauron.DTO.owner.subscription;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSubscriptionDTO {

    private String planName;
    private String status;     // ACTIVE, EXPIRED, TRIAL, NONE
    private String startDate;  // ISO date string
    private String endDate;    // ISO date string
    private long daysLeft;
    // private Boolean autoRenew; // optional
}