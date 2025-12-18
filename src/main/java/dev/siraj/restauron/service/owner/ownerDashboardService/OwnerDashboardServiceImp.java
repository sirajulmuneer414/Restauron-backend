package dev.siraj.restauron.service.owner.ownerDashboardService;

import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSalesStatsDTO;
import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSubscriptionDTO;
import dev.siraj.restauron.DTO.owner.dashboard.TopItemDTO;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.respository.orderRepo.OrderRepository;
import dev.siraj.restauron.respository.subscription.RestaurantSubscriptionRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

// Service implementation for OwnerDashboardService

@Service
@Slf4j
public class OwnerDashboardServiceImp implements OwnerDashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantSubscriptionRepository subscriptionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    /**
     * Retrieves sales statistics for a given restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return an OwnerDashboardSalesStatsDTO containing sales stats
     */
    @Override
    public OwnerDashboardSalesStatsDTO getSalesStats(Long restaurantId) {

        log.info("Fetching sales stats for restaurant ID: {}", restaurantId);

        LocalDate now = LocalDate.now();
        OwnerDashboardSalesStatsDTO stats = new OwnerDashboardSalesStatsDTO();

        stats.setToday(orderRepository.findTotalSalesSince(now, restaurantId));
        stats.setWeek(orderRepository.findTotalSalesSince(now.minusWeeks(1), restaurantId));
        stats.setMonth(orderRepository.findTotalSalesSince(now.withDayOfMonth(1), restaurantId));
        stats.setYear(orderRepository.findTotalSalesSince(now.withDayOfYear(1), restaurantId));

        log.info("Sales stats for restaurant ID {}: {}", restaurantId, stats);

        return stats;
    }

    /**
     * Retrieves subscription status for a given restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return an OwnerDashboardSubscriptionDTO containing subscription status
     */
    @Override
    public OwnerDashboardSubscriptionDTO getSubscriptionStatus(Long restaurantId) {

        log.info("Fetching subscription status for restaurant ID: {}", restaurantId);

        return subscriptionRepository.findFirstByRestaurantIdAndStatusOrderByEndDateDesc(restaurantId, SubscriptionStatus.ACTIVE)
                .map(sub -> {
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), sub.getEndDate());
                    return new OwnerDashboardSubscriptionDTO(
                            sub.getSubscriptionPackage().getName(),
                            "ACTIVE",
                            daysLeft,
                            sub.getEndDate());
                })
                .orElse(new OwnerDashboardSubscriptionDTO(null, "NONE", 0L, null));
    }

    /**
     * Retrieves the top 5 selling items for a given restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of TopItemDTO representing the top selling items
     */
    @Override
    public List<TopItemDTO> getTopSellingItems(Long restaurantId) {

        log.info("Fetching top selling items for restaurant ID: {}", restaurantId);

        List<Object[]> results = orderRepository.findTopSellingItemsByRestaurant(restaurantId, 5);
        return results.stream()
                .map(row -> new TopItemDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).doubleValue()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the count of employees for a given restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return the number of employees
     */
    @Override
    public long getEmployeeCount(Long restaurantId) {
        // Assumes you have a 'ROLE_EMPLOYEE' or similar in your User entity
        log.info("Fetching employee count for restaurant ID: {}", restaurantId);

        long employeeCount = employeeRepository.countByRestaurant_Id(restaurantId);

        log.info("Employee count for restaurant ID {}: {}", restaurantId, employeeCount);

        return employeeCount;
    }

    @Override
    public List<OrderDetailDto> findRecentOrders(long restaurantId) {

        log.info("Fetching recent orders for restaurant ID: {}", restaurantId);
        List<OrderDetailDto> recentOrders = orderRepository.findTop5ByRestaurantIdOrderByOrderDateDesc(restaurantId)
                .stream()
                .map(order -> {
                    OrderDetailDto dto = new OrderDetailDto();
                    if (order.getRestaurantTable() != null) {
                        dto.setRestaurantTableId(order.getRestaurantTable().getId());
                        dto.setRestaurantTableName(order.getRestaurantTable().getName());
                    } else {
                        dto.setRestaurantTableName("Take Away / N/A"); // Fallback to avoid NullPointerException
                    }
                    dto.setEncryptedOrderId(idEncryptionService.encryptLongId(order.getId()));
                    dto.setOrderDate(order.getOrderDate());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setOrderType(order.getOrderType());
                    dto.setStatus(order.getStatus());
                    dto.setOrderTime(order.getOrderTime());

                    if(order.getCustomer() != null) {
                        dto.setCustomerName(order.getCustomer().getUser().getName());
                        dto.setCustomerPhone(order.getCustomer().getUser().getPhone());
                    }else{
                        dto.setCustomerName(order.getTemporaryCustomerName());
                        dto.setCustomerPhone(order.getTemporaryCustomerNumber());
                    }

                    dto.setBillNumber(order.getBillNumber());
                    dto.setPaymentMode(order.getPaymentMode());


                    return dto;
                }  )
                .collect(Collectors.toList());

        log.info("Recent orders for restaurant ID {}: {}", restaurantId, recentOrders);

        return recentOrders;

    }
}
