package dev.siraj.restauron.service.publicApi.tableOrder;

import dev.siraj.restauron.DTO.publicApi.tableOrder.*;
import dev.siraj.restauron.entity.enums.AccessLevelStatus;
import dev.siraj.restauron.entity.enums.AvailabilityStatus;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.enums.PaymentMode;
import dev.siraj.restauron.entity.enums.table.TableStatus;
import dev.siraj.restauron.entity.menuManagement.Category;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.entity.orderManagement.OrderItem;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.repository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.repository.orderRepo.OrderRepository;
import dev.siraj.restauron.repository.restaurantManagementRepos.RestaurantTableRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.publicApi.tableOrder.interfaces.TableOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TableOrderServiceImp implements TableOrderService {

        private final RestaurantTableRepository tableRepository;
        private final MenuItemRepository menuItemRepository;
        private final OrderRepository orderRepository;
        private final IdEncryptionService idEncryptionService;

        @Autowired
        public TableOrderServiceImp(
                        RestaurantTableRepository tableRepository,
                        MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        IdEncryptionService idEncryptionService) {
                this.tableRepository = tableRepository;
                this.menuItemRepository = menuItemRepository;
                this.orderRepository = orderRepository;
                this.idEncryptionService = idEncryptionService;
        }

        @Override
        public TableOrderInfoResponseDTO getTableOrderInfo(String encryptedTableId) {
                log.info("Fetching table order info for encrypted table ID: {}", encryptedTableId);

                // Decrypt table ID
                Long tableId = idEncryptionService.decryptToLongId(encryptedTableId);

                // Fetch table
                RestaurantTable table = tableRepository.findById(tableId)
                                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

                // Check if table is available
                if (table.getStatus() != TableStatus.AVAILABLE) {
                        throw new IllegalArgumentException(
                                        "Table is not available for ordering. Current status: " + table.getStatus());
                }

                Restaurant restaurant = table.getRestaurant();

                // 🔒 Block ordering if subscription is expired or restaurant is blocked
                validateRestaurantAccess(restaurant);

                // Fetch available menu items
                List<MenuItem> availableMenuItems = menuItemRepository
                                .findByRestaurantAndIsAvailableTrueAndStatusOrderByCategoryName(restaurant,
                                                AvailabilityStatus.AVAILABLE);

                // Group menu items by category
                Map<Category, List<MenuItem>> itemsByCategory = availableMenuItems.stream()
                                .collect(Collectors.groupingBy(
                                                MenuItem::getCategory,
                                                LinkedHashMap::new,
                                                Collectors.toList()));

                // Build category DTOs
                List<CategoryWithItemsDTO> menuCategories = itemsByCategory.entrySet().stream()
                                .map(entry -> {
                                        Category category = entry.getKey();
                                        List<MenuItemPublicDTO> items = entry.getValue().stream()
                                                        .map(item -> new MenuItemPublicDTO(
                                                                        idEncryptionService.encryptLongId(item.getId()),
                                                                        item.getName(),
                                                                        item.getDescription(),
                                                                        item.getPrice(),
                                                                        item.getImageUrl(),
                                                                        item.isVegetarian(),
                                                                        category.getName()))
                                                        .collect(Collectors.toList());

                                        return new CategoryWithItemsDTO(
                                                        idEncryptionService.encryptLongId(category.getId()),
                                                        category.getName(),
                                                        category.getDescription(),
                                                        items);
                                })
                                .filter(category -> !category.items().isEmpty()) // Only include categories with
                                                                                 // available items
                                .collect(Collectors.toList());

                // Build response
                TableOrderInfoResponseDTO.TableInfoDTO tableInfo = new TableOrderInfoResponseDTO.TableInfoDTO(
                                encryptedTableId,
                                table.getName(),
                                table.getCapacity(),
                                table.getStatus());

                TableOrderInfoResponseDTO.RestaurantInfoDTO restaurantInfo = new TableOrderInfoResponseDTO.RestaurantInfoDTO(
                                idEncryptionService.encryptLongId(restaurant.getId()),
                                restaurant.getName());

                log.info("Successfully fetched {} categories with menu items for table {}", menuCategories.size(),
                                table.getName());

                return new TableOrderInfoResponseDTO(tableInfo, restaurantInfo, menuCategories);
        }

        @Override
        @Transactional
        public PlaceTableOrderResponseDTO placeTableOrder(String encryptedTableId, PlaceTableOrderRequestDTO request) {
                log.info("Placing order for table with encrypted ID: {}", encryptedTableId);

                // Decrypt table ID
                Long tableId = idEncryptionService.decryptToLongId(encryptedTableId);

                // Fetch table
                RestaurantTable table = tableRepository.findById(tableId)
                                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

                // Validate table is available
                if (table.getStatus() != TableStatus.AVAILABLE) {
                        throw new IllegalArgumentException("Table is not available for ordering");
                }

                Restaurant restaurant = table.getRestaurant();

                // 🔒 Block ordering if subscription is expired or restaurant is blocked
                validateRestaurantAccess(restaurant);

                // Create order
                Order order = new Order();
                order.setRestaurant(restaurant);
                order.setRestaurantTable(table);
                order.setTemporaryCustomerName(request.customerName());
                order.setTemporaryCustomerNumber(request.customerPhone());
                order.setCustomerRemarks(request.customerRemarks());
                order.setStatus(OrderStatus.PENDING);
                order.setOrderType(OrderType.DINE_IN);
                order.setPaymentMode(PaymentMode.CASH);

                // Process order items
                List<OrderItem> orderItems = new ArrayList<>();
                double totalAmount = 0.0;

                for (PlaceTableOrderRequestDTO.OrderItemDTO itemDTO : request.items()) {
                        Long menuItemId = idEncryptionService.decryptToLongId(itemDTO.encryptedMenuItemId());

                        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Menu item not found: " + itemDTO.encryptedMenuItemId()));

                        // Validate menu item belongs to this restaurant
                        if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                                throw new IllegalArgumentException("Menu item does not belong to this restaurant");
                        }

                        // Validate menu item is available
                        if (!menuItem.isAvailable() || menuItem.getStatus() != AvailabilityStatus.AVAILABLE) {
                                throw new IllegalArgumentException("Menu item is not available: " + menuItem.getName());
                        }

                        // Create order item
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrder(order);
                        orderItem.setMenuItem(menuItem);
                        orderItem.setQuantity(itemDTO.quantity());
                        orderItem.setPriceAtOrder(menuItem.getPrice());

                        orderItems.add(orderItem);
                        totalAmount += menuItem.getPrice() * itemDTO.quantity();
                }

                order.setItems(orderItems);
                order.setTotalAmount(totalAmount);

                // Save order (will trigger @PrePersist and @PostPersist for bill number)
                Order savedOrder = orderRepository.save(order);

                // Update bill number to final format (the @PostPersist sets it in memory but
                // doesn't persist)
                savedOrder.setBillNumber("ORD-" + savedOrder.getId());
                savedOrder = orderRepository.save(savedOrder);

                // Update table status to OCCUPIED
                table.setStatus(TableStatus.OCCUPIED);
                tableRepository.save(table);

                log.info("Order placed successfully: {} for table {} with {} items, total: ₹{}",
                                savedOrder.getBillNumber(), table.getName(), orderItems.size(), totalAmount);
                log.info("Table {} marked as OCCUPIED", table.getName());

                return new PlaceTableOrderResponseDTO(
                                savedOrder.getBillNumber(),
                                savedOrder.getTotalAmount(),
                                savedOrder.getStatus(),
                                savedOrder.getOrderDate(),
                                savedOrder.getOrderTime(),
                                "Order placed successfully! Pay cash to the waiter.");
        }

        /**
         * Validates that the restaurant has an active subscription level that allows
         * ordering.
         * READ_ONLY and BLOCKED states prevent any new orders from being placed.
         */
        private void validateRestaurantAccess(Restaurant restaurant) {
                AccessLevelStatus level = restaurant.getAccessLevel();
                if (level == AccessLevelStatus.READ_ONLY || level == AccessLevelStatus.BLOCKED) {
                        log.warn("Ordering blocked for restaurant '{}' due to access level: {}", restaurant.getName(),
                                        level);
                        throw new IllegalArgumentException(
                                        "Online ordering is currently unavailable. This restaurant's subscription has expired. "
                                                        +
                                                        "Please contact staff to place your order.");
                }
        }
}
