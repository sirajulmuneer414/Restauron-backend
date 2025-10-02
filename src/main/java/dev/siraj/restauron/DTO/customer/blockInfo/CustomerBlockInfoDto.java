package dev.siraj.restauron.DTO.customer.blockInfo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerBlockInfoDto {
    private String subject;
    private String description;
    private LocalDateTime blockedAt;
    private boolean hasPendingRequest;
    private String restaurantName;
}
