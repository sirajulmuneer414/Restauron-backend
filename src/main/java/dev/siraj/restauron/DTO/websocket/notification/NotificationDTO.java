package dev.siraj.restauron.DTO.websocket.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

    // Data Transfer Object for WebSocket notifications

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

        private String title;
        private String message;
        private String type; // INFO, WARNING, SUCCESS, ALERT
        private LocalDateTime timestamp;


}
