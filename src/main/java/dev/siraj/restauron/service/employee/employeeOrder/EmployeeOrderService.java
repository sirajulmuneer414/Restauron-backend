package dev.siraj.restauron.service.employee.employeeOrder;

import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeOrderService {
    List<OrderSummaryDto> getActiveOrders(String encryptedRestaurantId);

    Page<OrderSummaryDto> getOrdersPage(String encryptedRestaurantId, OrderPageRequestDto dto);
}
