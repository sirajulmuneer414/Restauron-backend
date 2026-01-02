package dev.siraj.restauron.DTO.customer.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderSummaryDTO {
    private String encryptedOrderId;
    private String billNumber;
    private String restaurantName;
    private String status;
    private Double totalAmount;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private Integer itemCount;
}