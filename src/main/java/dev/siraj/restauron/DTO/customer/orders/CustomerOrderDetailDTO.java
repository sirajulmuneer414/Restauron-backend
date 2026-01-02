package dev.siraj.restauron.DTO.customer.orders;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderDetailDTO {
    private String encryptedOrderId;
    private String billNumber;
    private String restaurantName;
    private String orderType;
    private String status;
    private Double totalAmount;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private String tableName; // Null for takeaway
    private List<OrderItemResponse> items;
    private String customerRemarks;
}