package dev.siraj.restauron.DTO.orders;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String restaurantEncryptedId;
    private List<OrderItemRequest> items;
    private String orderType;
    private String paymentMode;
    private String customerRemarks;
    private LocalDate scheduledDate; // For reservations
    private LocalTime scheduledTime; // For reservations
}