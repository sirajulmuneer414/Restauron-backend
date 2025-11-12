package dev.siraj.restauron.DTO.orders;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String menuItemEncryptedId;
    private int quantity;
}
