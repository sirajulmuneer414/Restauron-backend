package dev.siraj.restauron.DTO.owner.orderManagement;

import dev.siraj.restauron.DTO.customer.orders.OrderItemResponse;
import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderSummaryDto {
    private String encryptedId;

    private String billNumber;

    private String customerName;

    private String orderType;

    private String status;

    private String totalAmount;

    private LocalDate orderDate;

    private RestaurantTableDTO restaurantTable;

    private List<OrderItemResponse> items;
}
