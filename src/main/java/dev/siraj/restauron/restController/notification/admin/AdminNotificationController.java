package dev.siraj.restauron.restController.notification.admin;

import dev.siraj.restauron.DTO.websocket.notification.NotificationDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.websocket.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// Admin controller for sending notifications via WebSocket

@RestController
@RequestMapping("/admin/notify")
@RolesAllowed(roles = {"ADMIN"}) // Ensure security
public class AdminNotificationController {

        @Autowired
        private NotificationService notificationService;


    /**
     * Notify Specific User
     *
     * @param email email of the user to notify
     * @param req notification details
     * @return ResponseEntity with status
     */
        @PostMapping("/user/{email}")
        public ResponseEntity<String> notifyUser(
                @PathVariable String email,
                @RequestBody NotificationDTO req) {
            notificationService.sendPrivateNotification(email, req.getTitle(), req.getMessage());
            return ResponseEntity.ok("Notification sent to " + email);
        }

        /**
         * Notify All Owners
         *
         * @param req notification details
         * @return ResponseEntity with status
         */
        @PostMapping("/owners")
        public ResponseEntity<String> notifyOwners(@RequestBody NotificationDTO req) {
            notificationService.sendOwnerAnnouncement(req.getTitle(), req.getMessage());
            return ResponseEntity.ok("Notification sent to all owners");
        }

        /**
         * Broadcast to All Users
         *
         * @param req notification details
         * @return ResponseEntity with status
         */
        @PostMapping("/broadcast")
        public ResponseEntity<String> broadcast(@RequestBody NotificationDTO req) {
            notificationService.sendGlobalAnnouncement(req.getTitle(), req.getMessage());
            return ResponseEntity.ok("Broadcast sent");
        }
}
