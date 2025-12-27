package dev.siraj.restauron.DTO.websocket.notification;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    // DTO for sending order alerts via WebSocket notifications

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAlertDTO {
    private Long orderId;
    private String billNumber;
    private String tableNumber; // "N/A" for Take Away
    private String customerName;
    private String orderType;   // DINE_IN, TAKE_AWAY
    private Double totalAmount;
    private Integer itemCount;
    private String itemsSummary;
    private String type;// e.g., "2x Burger, 1x Coke..."
}