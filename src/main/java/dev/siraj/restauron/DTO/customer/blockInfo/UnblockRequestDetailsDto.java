package dev.siraj.restauron.DTO.customer.blockInfo;


import dev.siraj.restauron.entity.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UnblockRequestDetailsDto {
    private String requestEncryptedId;
    private String customerName;
    private String customerEmail;
    private String message; // Customer's message
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String ownerResponse; // Owner's response on rejection
}