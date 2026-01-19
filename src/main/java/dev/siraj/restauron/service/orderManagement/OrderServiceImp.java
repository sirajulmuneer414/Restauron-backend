package dev.siraj.restauron.service.orderManagement;

// DTOs
import dev.siraj.restauron.DTO.customer.orders.OrderItemResponse;
import dev.siraj.restauron.DTO.customer.orders.OrderResponse;
import dev.siraj.restauron.DTO.orders.CreateOrderRequest;
import dev.siraj.restauron.DTO.orders.OrderItemRequest;
import dev.siraj.restauron.DTO.orders.OrderPageRequestDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderSummaryDto;
import dev.siraj.restauron.DTO.common.orderManagement.OrderRequest;

// Entities
import dev.siraj.restauron.DTO.websocket.notification.OrderAlertDTO;
import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.enums.PaymentMode;
import dev.siraj.restauron.entity.enums.table.TableStatus;
import dev.siraj.restauron.entity.menuManagement.MenuItem;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.entity.orderManagement.OrderItem;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.entity.users.Customer;

// Repositories
import dev.siraj.restauron.repository.customerRepo.CustomerRepository;
import dev.siraj.restauron.repository.menuManagement.menuItemRepo.MenuItemRepository;
import dev.siraj.restauron.repository.orderRepo.OrderRepository;
import dev.siraj.restauron.repository.restaurantManagementRepos.RestaurantTableRepository;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.repository.userRepo.UserRepository;

// Services & Specifications
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.orderManagement.interfaces.OrderService;

// Exceptions
import dev.siraj.restauron.service.websocket.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;

// Spring & Java
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional // Apply transactionality at the class level
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final IdEncryptionService idEncryptionService;
    private final RestaurantTableRepository restaurantTableRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImp(OrderRepository orderRepository, CustomerRepository customerRepository,
                           RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository,
                           IdEncryptionService idEncryptionService,
                           RestaurantTableRepository restaurantTableRepository,
                           NotificationService notificationService,
                           UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.idEncryptionService = idEncryptionService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    //
    // --- OWNER-FACING METHODS ---
    //

    /**
     * Gets a paginated list of order summaries for the owner dashboard.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<OrderSummaryDto> getAllOrdersForOwner(String encryptedRestaurantId, OrderPageRequestDto dto) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        Pageable pageable = createPageable(dto);
        Specification<Order> spec = buildSpecification(restaurantId, dto);
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        return orderPage.map(this::convertToOrderSummaryDto);
    }

    /**
     * Gets full details for a single order.
     */
    @Transactional(readOnly = true)
    @Override
    public OrderDetailDto getOrderDetails(String encryptedOrderId) {
        Long orderId = idEncryptionService.decryptToLongId(encryptedOrderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        return toOrderDetailDto(order);
    }

    /**
     * Creates a manual order from the owner dashboard.
     */
    @Transactional
    @Override
    public OrderDetailDto createManualOrder(String encryptedRestaurantId, OrderRequest request) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found."));

        // 1. Create and set up the Order object
        Order order = new Order();
        order.setRestaurant(restaurant);

        // A. Handle Customer (Registered vs. Temporary)
        if (StringUtils.hasText(request.getCustomerEncryptedId())) {
            Long customerId = idEncryptionService.decryptToLongId(request.getCustomerEncryptedId());
            Customer customer = customerRepository.findById(customerId)
                    .orElse(null);
            order.setCustomer(customer);
        } else if (StringUtils.hasText(request.getCustomerName()) || StringUtils.hasText(request.getCustomerPhone())) {
            order.setTemporaryCustomerName(request.getCustomerName());
            order.setTemporaryCustomerNumber(request.getCustomerPhone());
        } else {
            throw new BadCredentialsException("A customer phone or a temporary name is required to create an order.");
        }

        // B. Handle Order Details (Status, Type, Payment)
        order.setOrderType(request.getOrderType());
        order.setPaymentMode(request.getPaymentMode());
        order.setStatus(request.getStatus());

        // C. Handle Table Selection for DINE_IN
        if (request.getOrderType() == OrderType.DINE_IN) {
            if (request.getTableId() == null) {
                throw new BadCredentialsException("A table must be selected for DINE_IN orders.");
            }
            RestaurantTable table = restaurantTableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new EntityNotFoundException("Table not found with ID: " + request.getTableId()));

            if (!table.getRestaurant().getId().equals(restaurantId)) {
                throw new BadCredentialsException("Selected table does not belong to this restaurant.");
            }
            order.setRestaurantTable(table);
            table.setStatus(TableStatus.OCCUPIED);
            restaurantTableRepository.save(table);
        }

        // 2. Process items
        Map<MenuItem, Integer> itemMap = processItems(
                request.getItems(),
                item -> idEncryptionService.decryptToLongId(item.getEncryptedId()),
                OrderRequest.ItemRequest::getQuantity
        );

        // 3. Pass the order to the helper to set items, total, and bill number
        Order savedOrder = buildAndSaveOrder(order, itemMap);

        // 4. Send real-time alert to restaurant staff
        try {
            sendRealTimeAlert(savedOrder);
        } catch (Exception e) {
            log.error("Failed to send real-time order alert for Order ID: {}", savedOrder.getId(), e);
        }

        return toOrderDetailDto(savedOrder);
    }

    /**
     * Updates only the status of an existing order.
     */
    @Transactional
    @Override
    public OrderDetailDto updateOrderStatus(String encryptedOrderId, String status, String encryptedRestaurantId) {
        Long orderId = idEncryptionService.decryptToLongId(encryptedOrderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            // 🔔 Send WebSocket notification to customer
            notificationService.sendOrderStatusUpdateToCustomer(
                    updatedOrder.getId(),
                    newStatus,
                    updatedOrder.getBillNumber()
            );
            notificationService.sendRefreshSignal(encryptedRestaurantId,"STATUS_UPDATE");

            if(newStatus == OrderStatus.COMPLETED && order.getOrderType() == OrderType.DINE_IN){
                RestaurantTable table = order.getRestaurantTable();
                if(table != null){
                    table.setStatus(TableStatus.AVAILABLE);
                    restaurantTableRepository.save(table);
                }
            }
            if(newStatus == OrderStatus.CANCELLED && order.getOrderType() == OrderType.DINE_IN){
                RestaurantTable table = order.getRestaurantTable();
                if(table != null ){
                    table.setStatus(TableStatus.AVAILABLE);
                    restaurantTableRepository.save(table);
                }
            }
            return toOrderDetailDto(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid status value provided: " + status);
        }
    }

    /**
     * Deletes an order.
     */
    @Transactional
    @Override
    public void deleteOrder(String encryptedOrderId) {
        Long orderId = idEncryptionService.decryptToLongId(encryptedOrderId);
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    //
    // --- CUSTOMER-FACING METHODS ---
    //

    /**
     * Creates an order from a customer-facing checkout.
     */
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String customerId) {
        Long customerLongId = idEncryptionService.decryptToLongId(customerId);
        Customer customer = customerRepository.findById(customerLongId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Long restaurantId = idEncryptionService.decryptToLongId(request.getRestaurantEncryptedId());
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        // 1. Create and set up the Order object
        Order order = new Order();
        order.setCustomer(customer); // Registered customer
        order.setRestaurant(restaurant);
        order.setOrderType(OrderType.valueOf(request.getOrderType()));
        order.setPaymentMode(PaymentMode.valueOf(request.getPaymentMode()));
        order.setStatus(OrderStatus.PENDING); // Customer orders are always PENDING
        order.setCustomerRemarks(request.getCustomerRemarks());
        order.setScheduledDate(request.getScheduledDate());
        order.setScheduledTime(request.getScheduledTime());

        // 2. Process items
        Map<MenuItem, Integer> itemMap = processItems(
                request.getItems(),
                item -> idEncryptionService.decryptToLongId(item.getMenuItemEncryptedId()),
                OrderItemRequest::getQuantity
        );

        // 3. Pass the order to the helper to set items, total, and bill number
        Order savedOrder = buildAndSaveOrder(order, itemMap);
        return toOrderResponse(savedOrder);
    }

    //
    // --- PRIVATE HELPER & CONVERTER METHODS ---
    //

    /**
     * REFACTORED HELPER
     * This method now ONLY handles item processing, total calculation,
     * and bill number generation for an ALREADY CONSTRUCTED order object.
     */
    private Order buildAndSaveOrder(Order order, Map<MenuItem, Integer> itemMap) {
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (Map.Entry<MenuItem, Integer> entry : itemMap.entrySet()) {
            MenuItem menuItem = entry.getKey();
            Integer quantity = entry.getValue();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtOrder(menuItem.getPrice());
            orderItems.add(orderItem);


            totalAmount += menuItem.getPrice() * quantity;
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // --- Safe Bill Number Generation ---
        order.setBillNumber("TEMP-" + java.util.UUID.randomUUID().toString()); // Set temporary
        Order savedOrder = orderRepository.save(order); // Save to get ID

        savedOrder.setBillNumber("ORD-" + savedOrder.getId()); // Set real bill number
        return orderRepository.save(savedOrder); // Save final
    }

    /**
     * A generic helper to process a list of any type of item request DTO.
     */
    private <T> Map<MenuItem, Integer> processItems(List<T> items,
                                                    Function<T, Long> idExtractor,
                                                    Function<T, Integer> quantityExtractor) {
        if (items == null || items.isEmpty()) {
            throw new BadCredentialsException("Order must contain at least one item.");
        }

        // Collect all menu item IDs
        List<Long> menuItemIds = items.stream()
                .map(idExtractor)
                .toList();

        // Fetch all MenuItems in a single query
        Map<Long, MenuItem> menuItemMap = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        // Build the final map of <MenuItem, Quantity>
        return items.stream().collect(Collectors.toMap(
                item -> {
                    Long id = idExtractor.apply(item);
                    MenuItem menuItem = menuItemMap.get(id);
                    if (menuItem == null) {
                        throw new EntityNotFoundException("Menu Item not found with ID: " + id);
                    }
                    return menuItem;
                },
                quantityExtractor
        ));
    }

    /**
     * Creates a Pageable object from the DTO, including sorting.
     */
    private Pageable createPageable(OrderPageRequestDto dto) {
        Sort sort = Sort.unsorted();

        if (StringUtils.hasText(dto.getSort())) {
            try {
                String[] parts = dto.getSort().split(",");
                String property = parts[0];
                Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.DESC;
                sort = Sort.by(direction, property);
            } catch (Exception e) {
                sort = Sort.by(Sort.Direction.DESC, "orderDate");
            }
        } else {
            sort = Sort.by(Sort.Direction.DESC, "orderDate");
        }

        return PageRequest.of(dto.getPage(), dto.getSize(), sort);
    }

    /**
     *  Specification Builder
     * @param restaurantId
     * @param pageRequestDto
     * @return
     */
    private Specification<Order> buildSpecification(Long restaurantId, OrderPageRequestDto pageRequestDto) {


        return (root, query, criteriaBuilder) -> {
            // Root predicate - always filter by the owner's restaurant
            Predicate finalPredicate = criteriaBuilder.equal(root.get("restaurant").get("id"), restaurantId);

            // Adding status filter if provided and not "ALL"
            if(StringUtils.hasText(pageRequestDto.getStatus())){
                try{
                    OrderStatus status =
                            OrderStatus.valueOf(pageRequestDto.getStatus().toUpperCase());

                    // Join with userAll to access status
                    finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(root.get("status"),status));

                }catch (IllegalArgumentException e){
                    log.warn("Invalid status filter provided: {}", pageRequestDto.getStatus());

                }
            }
            if(StringUtils.hasText(pageRequestDto.getType())){
                try{
                    OrderType status =
                            OrderType.valueOf(pageRequestDto.getType().toUpperCase());


                    finalPredicate = criteriaBuilder.and(finalPredicate, criteriaBuilder.equal(root.get("orderType"),status));

                }catch (IllegalArgumentException e){
                    log.warn("Invalid type filter provided: {}", pageRequestDto.getType());

                }
            }

            // adding searching filter if provided
            if(StringUtils.hasText(pageRequestDto.getSearch())){
                Join<Order, List<OrderItem>> itemsJoin = root.join("items");
                Join<Order, Customer> customerJoin = root.join("customer");


                String searchPattern = "%"+pageRequestDto.getSearch().toLowerCase()+"%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("user").get("name")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("user").get("email")),searchPattern),
                        criteriaBuilder.like(itemsJoin.get("menuItem").get("name"), searchPattern)
                );

                finalPredicate = criteriaBuilder.and(finalPredicate, searchPredicate);
            }

            return finalPredicate;
        };
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

    /**
     * Converts an Order entity to the detailed DTO for the owner detail page.
     */
    private OrderDetailDto toOrderDetailDto(Order order) {
        OrderDetailDto dto = new OrderDetailDto();
        dto.setEncryptedOrderId(idEncryptionService.encryptLongId(order.getId()));
        dto.setBillNumber(order.getBillNumber());
        dto.setStatus(order.getStatus());
        dto.setOrderType(order.getOrderType());
        dto.setPaymentMode(order.getPaymentMode());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderTime(order.getOrderTime());

        if (order.getCustomer() != null) {
            dto.setCustomerName(order.getCustomer().getUser().getName());
            dto.setCustomerPhone(order.getCustomer().getUser().getPhone());
        } else {
            dto.setCustomerName(order.getTemporaryCustomerName());
            dto.setCustomerPhone(order.getTemporaryCustomerNumber());
        }

        if (order.getRestaurantTable() != null) {
            dto.setRestaurantTableName(order.getRestaurantTable().getName());
            dto.setRestaurantTableId(order.getRestaurantTable().getId());
        }

        List<OrderDetailDto.OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
            OrderDetailDto.OrderItemDto itemDto = new OrderDetailDto.OrderItemDto();
            itemDto.setMenuItemName(item.getMenuItem().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtOrder(item.getPriceAtOrder());
            itemDto.setItemTotal(item.getPriceAtOrder() * item.getQuantity());
            return itemDto;
        }).toList();
        dto.setItems(itemDtos);

        return dto;
    }

    /**
     * Converts an Order entity to the response DTO for the customer.
     */
    private OrderResponse toOrderResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setBillNumber(order.getBillNumber());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setCustomerName(order.getCustomer().getUser().getName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setOrderType(order.getOrderType());
        dto.setPaymentMode(order.getPaymentMode());
        dto.setCustomerRemarks(order.getCustomerRemarks());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderTime(order.getOrderTime());
        dto.setScheduledDate(order.getScheduledDate());
        dto.setScheduledTime(order.getScheduledTime());


        if (order.getRestaurantTable() != null) {
            dto.setRestaurantTableName(order.getRestaurantTable().getName());
            dto.setRestaurantTableId(order.getRestaurantTable().getId());
        } else {
            dto.setRestaurantTableName(null); // or "N/A" or "No Table"
            dto.setRestaurantTableId(null);
        }

        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            OrderItemResponse itemDto = new OrderItemResponse();
            itemDto.setMenuItemName(item.getMenuItem().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtOrder(item.getPriceAtOrder());
            return itemDto;
        }).toList();

        dto.setItems(itemResponses);

        return dto;
    }


    /**
     * Sends a real-time order alert via WebSocket to the restaurant staff.
     */
    private void sendRealTimeAlert(Order order) {
        OrderAlertDTO alert = new OrderAlertDTO();
        alert.setOrderId(order.getId());
        alert.setBillNumber(order.getBillNumber());
        alert.setOrderType(order.getOrderType().toString());
        alert.setTotalAmount(order.getTotalAmount());
        alert.setItemCount(order.getItems().size());

        // Handle Customer Name (Registered vs Temporary)
        if (order.getCustomer() != null) {
            alert.setCustomerName(order.getCustomer().getUser().getName());
        } else {
            alert.setCustomerName(order.getTemporaryCustomerName());
        }

        // Handle Table Number
        if (order.getRestaurantTable() != null) {
            alert.setTableNumber(String.valueOf(order.getRestaurantTable().getName()));
        } else {
            alert.setTableNumber("N/A");
        }

        // Create Items Summary (e.g., "2x Pizza, 1x Coke")
        String summary = order.getItems().stream()
                .limit(3) // Only show first 3 items to keep payload small
                .map(item -> item.getQuantity() + "x " + item.getMenuItem().getName())
                .collect(Collectors.joining(", "));

        if (order.getItems().size() > 3) {
            summary += " + " + (order.getItems().size() - 3) + " more";
        }
        alert.setItemsSummary(summary);
        alert.setType("NEW_ORDER");

        // Send!
        notificationService.sendOrderAlert(order.getRestaurant().getId(), alert);
    }
}