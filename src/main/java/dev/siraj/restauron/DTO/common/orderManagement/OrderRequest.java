package dev.siraj.restauron.DTO.common.orderManagement;

import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.enums.PaymentMode;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    // Customer Info (for finding or creating a customer)
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private String customerEncryptedId;

    // Order Details
    private OrderType orderType;
    private PaymentMode paymentMode;
    private OrderStatus status; // e.g., CONFIRMED, PENDING
    private Long tableId;

    // Items in the Order
    private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        private String encryptedId;
        private int quantity;
    }
}
