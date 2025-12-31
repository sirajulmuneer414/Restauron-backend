package dev.siraj.restauron.restController.customer.orderController;

import dev.siraj.restauron.DTO.customer.orders.OrderResponse;
import dev.siraj.restauron.DTO.orders.CreateOrderRequest;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.service.orderManagement.interfaces.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/customer/orders")
@RolesAllowed(roles = {"CUSTOMER"})
@Slf4j
public class CustomerOrderController {

    private final OrderService orderService;

    @Autowired
    public CustomerOrderController(OrderService orderService) {
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

}
