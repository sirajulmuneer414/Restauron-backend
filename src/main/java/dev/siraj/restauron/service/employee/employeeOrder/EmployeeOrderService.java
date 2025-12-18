package dev.siraj.restauron.service.employee.employeeOrder;

import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;

import java.util.List;

public interface EmployeeOrderService {
    List<OrderSummaryDto> getActiveOrders(String encryptedRestaurantId);
}
