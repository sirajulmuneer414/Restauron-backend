package dev.siraj.restauron.service.employee.employeeOrder;

import dev.siraj.restauron.DTO.customer.orders.OrderItemResponse;
import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.repository.orderRepo.OrderRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeOrderServiceImp implements EmployeeOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    // -----------------------------------------------------------------------
    // getActiveOrders – used by Kitchen Display
    // -----------------------------------------------------------------------
    @Override
    public List<OrderSummaryDto> getActiveOrders(String encryptedRestaurantId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        Specification<Order> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("restaurant").get("id"), restaurantId),
                root.get("status").in(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY));

        return orderRepository.findAll(spec).stream()
                .map(this::convertToOrderSummaryActiveOrders)
                .toList();
    }

    // -----------------------------------------------------------------------
    // getOrdersPage – paginated list with search / filter / sort
    // -----------------------------------------------------------------------
    @Override
    public Page<OrderSummaryDto> getOrdersPage(String encryptedRestaurantId, OrderPageRequestDto dto) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        Pageable pageable = buildPageable(dto);
        Specification<Order> spec = buildEmployeeSpecification(restaurantId, dto);
        return orderRepository.findAll(spec, pageable).map(this::convertToOrderSummaryDto);
    }

    // -----------------------------------------------------------------------
    // PRIVATE HELPERS
    // -----------------------------------------------------------------------

    private Pageable buildPageable(OrderPageRequestDto dto) {
        Sort sort = Sort.by(Sort.Direction.DESC, "orderDate");
        if (StringUtils.hasText(dto.getSort())) {
            try {
                String[] parts = dto.getSort().split(",");
                Sort.Direction dir = parts.length > 1
                        ? Sort.Direction.fromString(parts[1])
                        : Sort.Direction.DESC;
                sort = Sort.by(dir, parts[0]);
            } catch (Exception ignored) {
                log.warn("Invalid sort param: {}", dto.getSort());
            }
        }
        return PageRequest.of(dto.getPage(), dto.getSize(), sort);
    }

    /**
     * Specification for the employee order list.
     * Uses LEFT JOIN on customer so walk-in orders are never excluded from search.
     */
    private Specification<Order> buildEmployeeSpecification(Long restaurantId, OrderPageRequestDto dto) {
        return (root, query, cb) -> {

            Predicate predicate = cb.equal(root.get("restaurant").get("id"), restaurantId);

            // --- Status filter ---
            if (StringUtils.hasText(dto.getStatus())) {
                try {
                    OrderStatus status = OrderStatus.valueOf(dto.getStatus().toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", dto.getStatus());
                }
            }

            // --- Order type filter ---
            if (StringUtils.hasText(dto.getType())) {
                try {
                    OrderType type = OrderType.valueOf(dto.getType().toUpperCase());
                    predicate = cb.and(predicate, cb.equal(root.get("orderType"), type));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid type filter: {}", dto.getType());
                }
            }

            // --- Full-text search ---
            if (StringUtils.hasText(dto.getSearch())) {
                // LEFT JOIN keeps walk-in rows that have no Customer entity
                var customerJoin = root.join("customer", JoinType.LEFT);
                String pattern = "%" + dto.getSearch().toLowerCase() + "%";

                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(customerJoin.get("user").get("name")), pattern),
                        cb.like(cb.lower(root.get("temporaryCustomerName")), pattern),
                        cb.like(cb.lower(root.get("billNumber")), pattern));
                predicate = cb.and(predicate, searchPredicate);
            }

            if (query != null)
                query.distinct(true);
            return predicate;
        };
    }

    // -----------------------------------------------------------------------
    // DTO CONVERTERS
    // -----------------------------------------------------------------------

    private OrderSummaryDto convertToOrderSummaryDto(Order order) {
        OrderSummaryDto dto = new OrderSummaryDto();
        dto.setEncryptedId(idEncryptionService.encryptLongId(order.getId()));
        dto.setBillNumber(order.getBillNumber());
        dto.setCustomerName(resolveCustomerName(order));
        dto.setOrderType(order.getOrderType().name());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount().toString());
        dto.setOrderDate(LocalDateTime.of(
                order.getOrderDate(),
                order.getOrderTime() != null ? order.getOrderTime() : LocalTime.MIDNIGHT));
        return dto;
    }

    private OrderSummaryDto convertToOrderSummaryActiveOrders(Order order) {
        OrderSummaryDto dto = convertToOrderSummaryDto(order);

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
                    order.getRestaurantTable().getStatus().name(),
                    order.getRestaurantTable().getCapacity()));
        }

        return dto;
    }

    private String resolveCustomerName(Order order) {
        if (order.getCustomer() != null) {
            return order.getCustomer().getUser().getName();
        }
        return (order.getTemporaryCustomerName() != null ? order.getTemporaryCustomerName() : "Unknown") + " (Walk-in)";
    }
}
