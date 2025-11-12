package dev.siraj.restauron.DTO.owner.orderManagement;

import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.enums.PaymentMode;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderDetailDto {
    private String encryptedOrderId;
    private String billNumber;
    private String customerName;
    private String customerPhone;
    private OrderStatus status;
    private OrderType orderType;
    private PaymentMode paymentMode;
    private Double totalAmount;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private List<OrderItemDto> items;
    private Long restaurantTableId;
    private String restaurantTableName;

    @Data
    public static class OrderItemDto {
        private String encryptedMenuItemId;
        private String menuItemName;
        private int quantity;
        private Double priceAtOrder;
        private Double itemTotal;
    }
}