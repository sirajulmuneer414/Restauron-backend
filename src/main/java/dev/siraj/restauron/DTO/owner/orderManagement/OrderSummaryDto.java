package dev.siraj.restauron.DTO.owner.orderManagement;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderSummaryDto {
    private String encryptedId;

    private String billNumber;

    private String customerName;

    private String orderType;

    private String status;

    private String totalAmount;

    private LocalDate orderDate;
}
