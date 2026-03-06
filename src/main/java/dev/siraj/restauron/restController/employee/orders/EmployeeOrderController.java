package dev.siraj.restauron.restController.employee.orders;

import dev.siraj.restauron.DTO.common.orderManagement.OrderRequest;
import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employee.employeeOrder.EmployeeOrderService;
import dev.siraj.restauron.service.orderManagement.interfaces.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/orders")
@RolesAllowed(roles = { "EMPLOYEE" })
@Slf4j
public class EmployeeOrderController {

    private final EmployeeOrderService employeeOrderService;
    private final OrderService orderService;

    @Autowired
    public EmployeeOrderController(EmployeeOrderService employeeOrderService, OrderService orderService) {
        this.employeeOrderService = employeeOrderService;
        this.orderService = orderService;
    }

    /** POST /api/employee/orders — create a manual order from the POS screen. */
    @PostMapping()
    public ResponseEntity<OrderDetailDto> createOrder(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody OrderRequest request) {
        log.info("Employee creating manual order");
        return ResponseEntity.ok(orderService.createManualOrder(encryptedRestaurantId, request));
    }

    /**
     * POST /api/employee/orders/list — paginated order list with search / filter /
     * sort.
     */
    @PostMapping("/list")
    public ResponseEntity<Page<OrderSummaryDto>> getOrdersPage(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody OrderPageRequestDto dto) {
        log.info("Employee fetching orders page – status:{} type:{} search:{} sort:{}",
                dto.getStatus(), dto.getType(), dto.getSearch(), dto.getSort());
        return ResponseEntity.ok(employeeOrderService.getOrdersPage(encryptedRestaurantId, dto));
    }

    /** GET /api/employee/orders/active — active orders for Kitchen Display. */
    @GetMapping("/active")
    public ResponseEntity<List<OrderSummaryDto>> getActiveOrders(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Employee fetching active orders");
        return ResponseEntity.ok(employeeOrderService.getActiveOrders(encryptedRestaurantId));
    }

    /** GET /api/employee/orders/{id} — full detail for one order. */
    @GetMapping("/{encryptedOrderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable String encryptedOrderId) {
        log.info("Employee fetching order detail {}", encryptedOrderId);
        return ResponseEntity.ok(orderService.getOrderDetails(encryptedOrderId));
    }

    /**
     * PUT /api/employee/orders/{id}/status?status=PREPARING — update order status.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailDto> updateOrderStatus(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable String id,
            @RequestParam String status) {
        log.info("Employee updating order {} to status {}", id, status);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status, encryptedRestaurantId));
    }

    /** PUT /api/employee/orders/{id}/items — replace order items. */
    @PutMapping("/{id}/items")
    public ResponseEntity<OrderDetailDto> updateOrderItems(
            @PathVariable String id,
            @RequestBody List<OrderRequest.ItemRequest> items) {
        log.info("Employee updating items for order {}", id);
        return ResponseEntity.ok(orderService.updateOrderItems(id, items));
    }

    /** DELETE /api/employee/orders/{id} — delete an order. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        log.info("Employee deleting order {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
