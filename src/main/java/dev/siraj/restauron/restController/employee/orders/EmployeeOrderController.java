package dev.siraj.restauron.restController.employee.orders;

import dev.siraj.restauron.DTO.common.orderManagement.OrderRequest;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employee.employeeOrder.EmployeeOrderService;
import dev.siraj.restauron.service.orderManagement.interfaces.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for Employee Order related endpoints

@RestController
@RequestMapping("/employee/orders")
@RolesAllowed(roles = {"EMPLOYEE"})
@Slf4j
public class EmployeeOrderController {

    @Autowired
    private EmployeeOrderService employeeOrderService;

    @Autowired
    private OrderService orderService;

    /**
     * Endpoint to create a manual order for a restaurant.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant from request header.
     * @param request               The order request payload.
     * @return ResponseEntity containing the created order details.
     */
    @PostMapping()
    public ResponseEntity<?> createOrder(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody OrderRequest request) {
        log.info("Inside the controller to create order from employee side");
        System.out.println(request.getTableId());
        for(var item : request.getItems()){
            System.out.println(item);
        }
        return ResponseEntity.ok(orderService.createManualOrder(encryptedRestaurantId, request));
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrderSummaryDto>> getActiveOrders(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ) {
        log.info("Inside the controller to get active orders for employee side");
        return ResponseEntity.ok(employeeOrderService.getActiveOrders(encryptedRestaurantId));
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailDto> updateOrderStatus(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable String id,
            @RequestParam String status) {
        log.info("Inside the controller to update order status for employee side {}", id);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status,encryptedRestaurantId));
    }

}
