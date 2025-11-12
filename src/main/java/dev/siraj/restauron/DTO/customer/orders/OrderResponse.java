package dev.siraj.restauron.DTO.customer.orders;


import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.enums.PaymentMode;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderResponse {
    private String billNumber;
    private String restaurantName;
    private String customerName;
    private List<OrderItemResponse> items;
    private double totalAmount;
    private OrderStatus status;
    private OrderType orderType;
    private PaymentMode paymentMode;
    private String customerRemarks;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String restaurantTableName;
    private Long restaurantTableId;
}