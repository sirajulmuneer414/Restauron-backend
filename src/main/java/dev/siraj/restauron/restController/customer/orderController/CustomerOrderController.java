package dev.siraj.restauron.restController.customer.orderController;

import dev.siraj.restauron.DTO.customer.orders.CustomerOrderDetailDTO;
import dev.siraj.restauron.DTO.customer.orders.CustomerOrderSummaryDTO;
import dev.siraj.restauron.DTO.customer.orders.OrderResponse;
import dev.siraj.restauron.DTO.customer.rating.MenuItemRatingStatsDTO;
import dev.siraj.restauron.DTO.customer.rating.RatingResponseDTO;
import dev.siraj.restauron.DTO.customer.rating.SubmitRatingDTO;
import dev.siraj.restauron.DTO.orders.CreateOrderRequest;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.customer.customerOrderService.CustomerOrderService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.orderManagement.interfaces.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/customer/orders")
@RolesAllowed(roles = {"CUSTOMER"})
@Slf4j
public class CustomerOrderController {

    private final OrderService orderService;

    private final IdEncryptionService idEncryptionService;

    private final CustomerOrderService customerOrderService;

    @Autowired
    public CustomerOrderController(OrderService orderService, IdEncryptionService idEncryptionService, CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
        this.idEncryptionService = idEncryptionService;
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request, @RequestHeader("X-Customer-Id") String customerId) {

        log.info("Inside the customer order controller with customer Id : {}",customerId);
        try {
            OrderResponse newOrder = orderService.createOrder(request, customerId);
            log.info("Successfully created the order and sending it to front-end");
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (Exception e) {


            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }



    /**
     * Get customer's order history
     * @param status "ACTIVE", "COMPLETED", or null for all
     */
    @GetMapping
    public ResponseEntity<List<CustomerOrderSummaryDTO>> getCustomerOrders(
            @RequestParam(required = false) String status,
            @RequestHeader("X-Customer-Id") String encryptedCustomerId){

        Long customerId = getCustomerIdFromString(encryptedCustomerId);
        log.info("Fetching orders for customerId={}, status={}", customerId, status);

        List<CustomerOrderSummaryDTO> orders = customerOrderService.getCustomerOrders(customerId, status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order details
     */
    @GetMapping("/{encryptedOrderId}")
    public ResponseEntity<CustomerOrderDetailDTO> getOrderDetails(
            @PathVariable String encryptedOrderId,
            @RequestHeader("X-Customer-Id") String encryptedCustomerId) {

        Long customerId = getCustomerIdFromString(encryptedCustomerId);
        log.info("Fetching order details: orderId={}, customerId={}", encryptedOrderId, customerId);

        CustomerOrderDetailDTO orderDetail = customerOrderService.getOrderDetails(encryptedOrderId, customerId);
        return ResponseEntity.ok(orderDetail);
    }

    /**
     * Submit rating for a menu item in an order
     */
    @PostMapping("/{encryptedOrderId}/items/{encryptedMenuItemId}/rate")
    public ResponseEntity<RatingResponseDTO> submitRating(
            @PathVariable String encryptedOrderId,
            @PathVariable String encryptedMenuItemId,
            @Valid @RequestBody SubmitRatingDTO ratingDTO,
            @RequestHeader("X-Customer-Id") String encryptedCustomerId){

        Long customerId = getCustomerIdFromString(encryptedCustomerId);
        log.info("Submitting rating: orderId={}, menuItemId={}, rating={}",
                encryptedOrderId, encryptedMenuItemId, ratingDTO.getRating());

        RatingResponseDTO response = customerOrderService.submitRating(
                encryptedOrderId, encryptedMenuItemId, ratingDTO, customerId
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Get ratings for a menu item (public or authenticated)
     */
    @GetMapping("/menu-items/{encryptedMenuItemId}/ratings")
    public ResponseEntity<List<RatingResponseDTO>> getMenuItemRatings(
            @PathVariable String encryptedMenuItemId) {

        log.info("Fetching ratings for menuItemId={}", encryptedMenuItemId);
        List<RatingResponseDTO> ratings = customerOrderService.getMenuItemRatings(encryptedMenuItemId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Get rating stats for a menu item
     */
    @GetMapping("/menu-items/{encryptedMenuItemId}/rating-stats")
    public ResponseEntity<MenuItemRatingStatsDTO> getMenuItemRatingStats(
            @PathVariable String encryptedMenuItemId) {

        log.info("Fetching rating stats for menuItemId={}", encryptedMenuItemId);
        MenuItemRatingStatsDTO stats = customerOrderService.getMenuItemRatingStats(encryptedMenuItemId);
        return ResponseEntity.ok(stats);
    }

    // Helper to extract customer ID from authentication
private Long getCustomerIdFromString(String encryptedCustomerId) {

        return idEncryptionService.decryptToLongId(encryptedCustomerId);
    }
}

