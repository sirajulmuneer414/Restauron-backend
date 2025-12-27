package dev.siraj.restauron.service.employee.employeeOrder;


import dev.siraj.restauron.DTO.customer.orders.OrderItemResponse;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.respository.orderRepo.OrderRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

// This is implementation for Employee Order Service

@Service
public class EmployeeOrderServiceImp implements EmployeeOrderService {


    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private IdEncryptionService idEncryptionService;


    @Override
    public List<OrderSummaryDto> getActiveOrders(String encryptedRestaurantId) {

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        Specification<Order> specification = (root, query, criteriaBuilder) -> {

            var statusPath = root.get("status");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId),
                    statusPath.in(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)
            );
        };

        List<Order> orderList = orderRepository.findAll(specification);

        return orderList.stream().map(this::convertToOrderSummaryActiveOrders).toList();

    }



    // --- DTO CONVERTERS ---

    /**
     * Converts an Order entity to its summary DTO for the owner list page.
     */
    private OrderSummaryDto convertToOrderSummaryDto(Order order) {
        String customerName;
        if (order.getCustomer() != null) {
            customerName = order.getCustomer().getUser().getName();
        } else {
            customerName = order.getTemporaryCustomerName() + " (Walk-in)";
        }

        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(order.getId()));
        dto.setBillNumber(order.getBillNumber());
        dto.setCustomerName(customerName);
        dto.setOrderType(order.getOrderType().name());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount().toString());
        dto.setOrderDate(
                LocalDateTime.of(
                        order.getOrderDate(),
                        order.getOrderTime() != null ? order.getOrderTime() : LocalTime.MIDNIGHT
                )
        );



        return dto;
    }

    private OrderSummaryDto convertToOrderSummaryActiveOrders(Order order) {
        String customerName;
        if (order.getCustomer() != null) {
            customerName = order.getCustomer().getUser().getName();
        } else {
            customerName = order.getTemporaryCustomerName() + " (Walk-in)";
        }

        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(order.getId()));
        dto.setBillNumber(order.getBillNumber());
        dto.setCustomerName(customerName);
        dto.setOrderType(order.getOrderType().name());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount().toString());
        dto.setOrderDate(
                LocalDateTime.of(
                        order.getOrderDate(),
                        order.getOrderTime() != null ? order.getOrderTime() : LocalTime.MIDNIGHT
                )
        );


        dto.setItems(order.getItems().stream().map(item -> {
            OrderItemResponse itemDto = new OrderItemResponse();
            itemDto.setMenuItemName(item.getMenuItem().getName());
            itemDto.setPriceAtOrder(item.getPriceAtOrder());
            itemDto.setQuantity(item.getQuantity());
            return itemDto;
        }).toList());

        if (order.getRestaurantTable() != null) {
            dto.setRestaurantTable(new RestaurantTableDTO(
                    order.getRestaurantTable().getId(),
                    order.getRestaurantTable().getName(),
                    order.getRestaurantTable().getStatus().name()
            ));
        }

        return dto;
    }

}
