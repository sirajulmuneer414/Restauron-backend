package dev.siraj.restauron.DTO.customer.orders;

import lombok.Data;

@Data
public class OrderItemResponse {
    private String encryptedMenuItemId;
    private String menuItemName;
    private int quantity;
    private double priceAtOrder;
}