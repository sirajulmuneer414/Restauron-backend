package dev.siraj.restauron.service.websocket.notification;

import dev.siraj.restauron.DTO.websocket.notification.NotificationDTO;
import dev.siraj.restauron.DTO.websocket.notification.OrderAlertDTO;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Service for sending WebSocket notifications to users

@Service
public class NotificationService {


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IdEncryptionService idEncryptionService;


    /**
     * Sends private notifications via WebSocket
     *
     * @param email   Recipient's email for private notifications
     * @param title   Title of the notification
     * @param message Body of the notification
     */
    public void sendPrivateNotification(String email, String title, String message) {
        NotificationDTO payload = new NotificationDTO(title, message, "INFO", LocalDateTime.now());

        // Sends to /user/{email}/queue/notifications
        // Note: 'email' must match the Principal name used in WebSocket connection
        messagingTemplate.convertAndSendToUser(email, "/queue/notifications", payload);
    }

    /**
     * Sends global announcements via WebSocket
     *
     * @param title   Title of the announcement
     * @param message Body of the announcement
     */
    public void sendGlobalAnnouncement(String title, String message) {
        NotificationDTO payload = new NotificationDTO(title, message, "ANNOUNCEMENT", LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/announcements", payload);
    }

    /**
     * Sends owner-specific announcements via WebSocket
     *
     * @param title   Title of the announcement
     * @param message Body of the announcement
     */
    public void sendOwnerAnnouncement(String title, String message) {
        NotificationDTO payload = new NotificationDTO(title, message, "OWNER_ALERT", LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/owners", payload);
    }

    /**
     * Sends a new order alert to a specific restaurant's staff
     *
     * @param restaurantId ID of the restaurant
     * @param orderAlert   Details of the new order alert
     */
    public void sendOrderAlert(Long restaurantId, OrderAlertDTO orderAlert) {
        // Destination: /topic/restaurant/{id}/orders
        String encryptedId = idEncryptionService.encryptLongId(restaurantId);
        String destination = "/topic/restaurant/" + encryptedId + "/orders";
        messagingTemplate.convertAndSend(destination, orderAlert);
    }

    /**
     * Sends a refresh signal to restaurant staff to update their order lists
     *
     * @param restaurantId ID of the restaurant
     * @param context      Context of the refresh (e.g., "STATUS_UPDATE", "ITEM_ADDED")
     */
    public void sendRefreshSignal(String restaurantId, String context) {

        String destination = "/topic/restaurant/" + restaurantId + "/orders";

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "REFRESH_ORDERS");
        payload.put("context", context); // e.g., "STATUS_UPDATE", "ITEM_ADDED"
        payload.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend(destination, payload);
    }



    /**
     * ✨ NEW: Sends order status update to customer subscribed to specific order
     *
     * @param orderId     The ID of the order (will be encrypted)
     * @param orderStatus The new status of the order
     * @param billNumber  The bill number for display (optional)
     */
    public void sendOrderStatusUpdateToCustomer(Long orderId, OrderStatus orderStatus, String billNumber) {
        String encryptedOrderId = idEncryptionService.encryptLongId(orderId);
        String destination = "/topic/order/" + encryptedOrderId;

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", encryptedOrderId);
        payload.put("status", orderStatus.name());
        payload.put("orderStatus", orderStatus.name()); // For compatibility
        payload.put("billNumber", billNumber);
        payload.put("timestamp", LocalDateTime.now().toString());
        payload.put("message", "Order status updated to " + orderStatus.name());

        messagingTemplate.convertAndSend(destination, payload);

        System.out.println("📤 Sent order status update: " + destination + " -> " + orderStatus.name());
    }

    /**
     * ✨ ALTERNATIVE: Send order status update with full order details
     * Use this if you want to send more information to the customer
     */
    public void sendDetailedOrderStatusUpdate(Long orderId, OrderStatus orderStatus,
                                              String billNumber, String restaurantName,
                                              Double totalAmount) {
        String encryptedOrderId = idEncryptionService.encryptLongId(orderId);
        String destination = "/topic/order/" + encryptedOrderId;

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", encryptedOrderId);
        payload.put("status", orderStatus.name());
        payload.put("orderStatus", orderStatus.name());
        payload.put("billNumber", billNumber);
        payload.put("restaurantName", restaurantName);
        payload.put("totalAmount", totalAmount);
        payload.put("timestamp", LocalDateTime.now().toString());
        payload.put("message", "Order status updated to " + orderStatus.name());

        messagingTemplate.convertAndSend(destination, payload);

        System.out.println("📤 Sent detailed order update: " + destination + " -> " + orderStatus.name());
    }

}
