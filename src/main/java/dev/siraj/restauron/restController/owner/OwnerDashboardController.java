package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSalesStatsDTO;
import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSubscriptionDTO;
import dev.siraj.restauron.DTO.owner.dashboard.TopItemDTO;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.owner.ownerDashboardService.OwnerDashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

    // REST controller for Owner Dashboard related endpoints

@RestController
@RequestMapping("/owner/dashboard")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class OwnerDashboardController {

    @Autowired
    private OwnerDashboardService ownerDashboardService;

    @Autowired
    private IdEncryptionService idEncryptionService;


    /** Endpoint to get sales statistics for the owner's restaurant.
     *
     * @param encryptedRestaurantId Encrypted restaurant ID from request header
     * @return ResponseEntity containing OwnerDashboardSalesStatsDTO
     */
    @GetMapping("/stats/sales")
    public ResponseEntity<OwnerDashboardSalesStatsDTO> getSalesStats(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Received request for sales stats for restaurant ID: {}", encryptedRestaurantId);
        long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        return ResponseEntity.ok(ownerDashboardService.getSalesStats(restaurantId));
    }

    /** Endpoint to get subscription status for the owner's restaurant.
     *
     * @param encryptedRestaurantId Encrypted restaurant ID from request header
     * @return ResponseEntity containing OwnerDashboardSubscriptionDTO
     */
    @GetMapping("/subscription/status")
    public ResponseEntity<OwnerDashboardSubscriptionDTO> getSubscriptionStatus(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Received request for subscription status for restaurant ID: {}", encryptedRestaurantId);
        long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        return ResponseEntity.ok(ownerDashboardService.getSubscriptionStatus(restaurantId));
    }

    /** Endpoint to get recent orders for the owner's restaurant.
     *
     * @param encryptedRestaurantId Encrypted restaurant ID from request header
     * @return ResponseEntity containing list of OrderDetailDto
     */
    @GetMapping("/orders/recent")
    public ResponseEntity<List<OrderDetailDto>> getRecentOrders(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Received request for recent orders for restaurant ID: {}", encryptedRestaurantId);
        long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        List<OrderDetailDto> recentOrders = ownerDashboardService.findRecentOrders(restaurantId);
        return ResponseEntity.ok(recentOrders);
    }

    /** Endpoint to get employee count for the owner's restaurant.
     *
     * @param encryptedRestaurantId Encrypted restaurant ID from request header
     * @return ResponseEntity containing employee count as Long
     */
    @GetMapping("/stats/employees")
    public ResponseEntity<Long> getEmployeeCount(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Received request for employee count for restaurant ID: {}", encryptedRestaurantId);
        long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        return ResponseEntity.ok(ownerDashboardService.getEmployeeCount(restaurantId));
    }

    /** Endpoint to get top-selling items for the owner's restaurant.
     *
     * @param encryptedRestaurantId Encrypted restaurant ID from request header
     * @return ResponseEntity containing list of TopItemDTO
     */
    @GetMapping("/stats/top-items")
    public ResponseEntity<List<TopItemDTO>> getTopItems(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Received request for top selling items for restaurant ID: {}", encryptedRestaurantId);
        long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        return ResponseEntity.ok(ownerDashboardService.getTopSellingItems(restaurantId));
    }
}
