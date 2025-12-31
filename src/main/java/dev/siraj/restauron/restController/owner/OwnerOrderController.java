package dev.siraj.restauron.restController.owner;


import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.DTO.common.orderManagement.OrderRequest;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.orderManagement.interfaces.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/orders")
@RolesAllowed(roles = {"OWNER"}) // Assuming you have role-based security
@Slf4j
public class OwnerOrderController {

    private final OrderService orderService;


    @Autowired
    public OwnerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST /owner/orders
    @PostMapping
    public ResponseEntity<Page<OrderSummaryDto>> getAllOrders(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody OrderPageRequestDto pageable) {

        log.info("Inside the controller to fetch lists of orders");
        System.out.println(pageable.getSort());
        System.out.println(pageable.getType());
        System.out.println(pageable.getSearch());
        System.out.println(pageable.getStatus());

        Page<OrderSummaryDto> page = orderService.getAllOrdersForOwner(encryptedRestaurantId, pageable);

        log.info("fetched successfully");
        return ResponseEntity.ok(page);
    }

    // GET /api/owner/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderById(@PathVariable("orderId") String encryptedOrderId) {

        OrderDetailDto dto = orderService.getOrderDetails(encryptedOrderId);

        return ResponseEntity.ok(dto);
    }

    // POST /api/owner/orders/create
    @PostMapping("/create")
    public ResponseEntity<OrderDetailDto> createManualOrder(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody OrderRequest request) {

        log.info("Inside the controller to create order from owner side");
        System.out.println(request.getTableId());
        System.out.println(request.getOrderType());
        System.out.println(request.getCustomerName());
        System.out.println(request.getStatus());
        System.out.println(request.getCustomerEncryptedId());

        request.getItems().forEach(System.out::println);



            OrderDetailDto dto = orderService.createManualOrder(encryptedRestaurantId, request);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // PUT /owner/orders/{orderId}/status
    @PutMapping("/status/{orderId}")
    public ResponseEntity<OrderDetailDto > updateOrderStatus(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable String orderId,
            @RequestParam("status") String orderStatus) {

        log.info("Inside the method to update order status for owner module");

        return ResponseEntity.ok(orderService.updateOrderStatus(orderId,orderStatus, encryptedRestaurantId));
    }

    // DELETE /api/owner/orders/{orderId}
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String encryptedOrderId) {
        orderService.deleteOrder(encryptedOrderId);
        return ResponseEntity.noContent().build();
    }
}



