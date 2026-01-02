package dev.siraj.restauron.service.customer.customerOrderService;

import dev.siraj.restauron.DTO.customer.orders.CustomerOrderDetailDTO;
import dev.siraj.restauron.DTO.customer.orders.CustomerOrderSummaryDTO;
import dev.siraj.restauron.DTO.customer.orders.OrderItemResponse;
import dev.siraj.restauron.DTO.customer.rating.MenuItemRatingStatsDTO;
import dev.siraj.restauron.DTO.customer.rating.RatingResponseDTO;
import dev.siraj.restauron.DTO.customer.rating.SubmitRatingDTO;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.entity.rating.MenuItemRating;
import dev.siraj.restauron.entity.users.Customer;
import dev.siraj.restauron.repository.customerRepo.CustomerRepository;
import dev.siraj.restauron.repository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.repository.orderRepo.OrderRepository;
import dev.siraj.restauron.repository.rating.MenuItemRatingRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerOrderServiceImp implements CustomerOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuItemRatingRepository ratingRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Override
    public List<CustomerOrderSummaryDTO> getCustomerOrders(Long customerId, String status) {
        List<Order> orders;

        if ("ACTIVE".equalsIgnoreCase(status)) {
            // Active = PENDING, PREPARING, READY
            orders = orderRepository.findByCustomerIdAndStatusIn(
                    customerId,
                    List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)
            );
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            orders = orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.COMPLETED);
        } else {
            // All orders
            orders = orderRepository.findByCustomerIdOrderByOrderDateDescOrderTimeDesc(customerId);
        }

        return orders.stream()
                .map(this::convertToSummaryDTO)
                .toList();
    }

    @Override
    public CustomerOrderDetailDTO getOrderDetails(String encryptedOrderId, Long customerId) {
        Long orderId = idEncryptionService.decryptToLongId(encryptedOrderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify this order belongs to the customer
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized access to order");
        }

        return convertToDetailDTO(order);
    }

    @Override
    @Transactional
    public RatingResponseDTO submitRating(String encryptedOrderId, String encryptedMenuItemId,
                                          SubmitRatingDTO ratingDTO, Long customerId) {
        Long orderId = idEncryptionService.decryptToLongId(encryptedOrderId);
        Long menuItemId = idEncryptionService.decryptToLongId(encryptedMenuItemId);

        // Validate order belongs to customer
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Unauthorized: Order does not belong to this customer");
        }

        // Validate order is completed
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("Can only rate items from completed orders");
        }

        // Validate menu item was in this order
        boolean itemInOrder = order.getItems().stream()
                .anyMatch(item -> item.getMenuItem().getId().equals(menuItemId));

        if (!itemInOrder) {
            throw new RuntimeException("Menu item not found in this order");
        }

        // Check if already rated
        if (ratingRepository.existsByMenuItemIdAndCustomerIdAndOrderId(menuItemId, customerId, orderId)) {
            throw new RuntimeException("You have already rated this item for this order");
        }

        // Create rating
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        MenuItemRating rating = new MenuItemRating();
        rating.setMenuItem(menuItem);
        rating.setCustomer(customer);
        rating.setOrder(order);
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());

        MenuItemRating savedRating = ratingRepository.save(rating);

        log.info("Rating submitted: customerId={}, menuItemId={}, rating={}",
                customerId, menuItemId, ratingDTO.getRating());

        return convertToRatingResponseDTO(savedRating);
    }

    @Override
    public List<RatingResponseDTO> getMenuItemRatings(String encryptedMenuItemId) {
        Long menuItemId = idEncryptionService.decryptToLongId(encryptedMenuItemId);

        return ratingRepository.findByMenuItemIdOrderByCreatedAtDesc(menuItemId).stream()
                .map(this::convertToRatingResponseDTO)
                .toList();
    }

    @Override
    public MenuItemRatingStatsDTO getMenuItemRatingStats(String encryptedMenuItemId) {
        Long menuItemId = idEncryptionService.decryptToLongId(encryptedMenuItemId);

        Double avgRating = ratingRepository.getAverageRatingByMenuItemId(menuItemId);
        Long totalRatings = ratingRepository.countByMenuItemId(menuItemId);

        return new MenuItemRatingStatsDTO(
                avgRating != null ? avgRating : 0.0,
                totalRatings
        );
    }

    // ==================== Helper Methods ====================

    private CustomerOrderSummaryDTO convertToSummaryDTO(Order order) {
        CustomerOrderSummaryDTO dto = new CustomerOrderSummaryDTO();
        dto.setEncryptedOrderId(idEncryptionService.encryptLongId(order.getId()));
        dto.setBillNumber(order.getBillNumber());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderTime(order.getOrderTime());
        dto.setItemCount(order.getItems().size());
        return dto;
    }

    private CustomerOrderDetailDTO convertToDetailDTO(Order order) {
        CustomerOrderDetailDTO dto = new CustomerOrderDetailDTO();
        dto.setEncryptedOrderId(idEncryptionService.encryptLongId(order.getId()));
        dto.setBillNumber(order.getBillNumber());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setOrderType(order.getOrderType().name());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderTime(order.getOrderTime());
        dto.setTableName(order.getRestaurantTable() != null ? order.getRestaurantTable().getName() : null);
        dto.setCustomerRemarks(order.getCustomerRemarks());

        // Map items
        List<OrderItemResponse> items = order.getItems().stream().map(item -> {
            OrderItemResponse itemDTO = new OrderItemResponse();
            itemDTO.setEncryptedMenuItemId(idEncryptionService.encryptLongId(item.getMenuItem().getId()));
            itemDTO.setMenuItemName(item.getMenuItem().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPriceAtOrder(item.getPriceAtOrder());
            return itemDTO;
        }).toList();

        dto.setItems(items);
        return dto;
    }

    private RatingResponseDTO convertToRatingResponseDTO(MenuItemRating rating) {
        RatingResponseDTO dto = new RatingResponseDTO();
        dto.setEncryptedRatingId(idEncryptionService.encryptLongId(rating.getId()));
        dto.setMenuItemName(rating.getMenuItem().getName());
        dto.setCustomerName(rating.getCustomer().getUser().getName());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }
}