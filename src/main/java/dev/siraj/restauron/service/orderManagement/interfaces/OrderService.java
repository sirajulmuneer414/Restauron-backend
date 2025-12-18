package dev.siraj.restauron.service.orderManagement.interfaces;

import dev.siraj.restauron.DTO.customer.orders.OrderResponse;
import dev.siraj.restauron.DTO.orders.CreateOrderRequest;
import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.DTO.common.orderManagement.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    @Transactional(readOnly = true)
    Page<OrderSummaryDto> getAllOrdersForOwner(String encryptedRestaurantId, OrderPageRequestDto dto);

    @Transactional(readOnly = true)
    OrderDetailDto getOrderDetails(String encryptedOrderId);

    @Transactional
    OrderDetailDto createManualOrder(String encryptedRestaurantId, OrderRequest request);

    @Transactional
    OrderDetailDto updateOrderStatus(String encryptedOrderId, String status, String encryptedRestaurantId);

    @Transactional
    void deleteOrder(String encryptedOrderId);

    OrderResponse createOrder(CreateOrderRequest request, String customerId);
}