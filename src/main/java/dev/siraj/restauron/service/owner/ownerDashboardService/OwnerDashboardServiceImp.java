package dev.siraj.restauron.service.owner.ownerDashboardService;

import dev.siraj.restauron.DTO.owner.customerSideUrl.RestaurantLinkDTO;
import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSalesStatsDTO;
import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSubscriptionDTO;
import dev.siraj.restauron.DTO.owner.dashboard.SalesReportDTO;
import dev.siraj.restauron.DTO.owner.dashboard.TopItemDTO;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.repository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.repository.orderRepo.OrderRepository;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.repository.subscription.RestaurantSubscriptionRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
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

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

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
                .toList();
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
                .toList();

        log.info("Recent orders for restaurant ID {}: {}", restaurantId, recentOrders);

        return recentOrders;

    }

    @Override
    public RestaurantLinkDTO getRestaurantCustomerLink(String encryptedRestaurantId) {


        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId); // Your existing method
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));



            String encryptedId = idEncryptionService.encryptLongId(restaurant.getId());



            if(restaurant.getCustomerPageUrl() == null) {
                String customerUrl = frontendUrl + "/restaurant/" + encryptedId + "/home";

                restaurant.setCustomerPageUrl(customerUrl);

                restaurant = restaurantRepository.save(restaurant);
            }



        return new RestaurantLinkDTO(
                restaurant.getCustomerPageUrl(),
                idEncryptionService.encryptLongId(restaurant.getId()),
                restaurant.getName()
        );
    }



    @Override
    public SalesReportDTO getSalesReport(Long restaurantId, String type) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        // Determine date range based on type
        switch (type.toUpperCase()) {
            case "WEEKLY":
                startDate = endDate.minusWeeks(12); // Last 12 weeks
                break;
            case "MONTHLY":
                startDate = endDate.minusMonths(12); // Last 12 months
                break;
            case "YEARLY":
                startDate = endDate.minusYears(5); // Last 5 years
                break;
            case "DAILY":
            default:
                startDate = endDate.minusDays(30); // Last 30 days
                break;
        }

        // Fetch Raw Data
        List<Order> orders = orderRepository.findAllByRestaurantIdAndStatusAndOrderDateBetween(
                restaurantId,
                dev.siraj.restauron.entity.enums.OrderStatus.COMPLETED,
                startDate,
                endDate
        );

        // Calculate Summary Metrics
        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        long totalCount = orders.size();
        double avgOrderValue = totalCount > 0 ? totalRevenue / totalCount : 0.0;

        // Group Data for Chart
        List<SalesReportDTO.SalesDataPoint> chartData = new ArrayList<>();
        Map<String, Double> groupedData = new TreeMap<>(); // Preserves order naturally if keys are sortable

        if ("DAILY".equalsIgnoreCase(type)) {
            // Fill map with 0.0 for all days to ensure continuous graph
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                groupedData.put(date.toString(), 0.0);
            }
            // Aggregate actual data
            orders.forEach(o -> groupedData.merge(o.getOrderDate().toString(), o.getTotalAmount(), Double::sum));

            // Convert to List
            groupedData.forEach((key, value) -> chartData.add(new SalesReportDTO.SalesDataPoint(
                    LocalDate.parse(key).getDayOfMonth() + "/" + LocalDate.parse(key).getMonthValue(), // Label: 12/10
                    value,
                    key
            )));

        } else if ("WEEKLY".equalsIgnoreCase(type)) {
            // Group by Week of Year
            Map<String, Double> weeklyMap = orders.stream()
                    .collect(Collectors.groupingBy(
                            o -> {
                                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                                int week = o.getOrderDate().get(weekFields.weekOfWeekBasedYear());
                                return "W" + week; // Label: W42
                            },
                            Collectors.summingDouble(Order::getTotalAmount)
                    ));

            // Simple conversion (sorting might need refinement based on year overlap in real prod, keeping simple here)
            weeklyMap.forEach((k,v) -> chartData.add(new SalesReportDTO.SalesDataPoint(k, v, k)));

        } else if ("MONTHLY".equalsIgnoreCase(type)) {
            // Group by Month-Year
            orders.forEach(o -> {
                String key = o.getOrderDate().getMonth().name().substring(0,3) + "-" + o.getOrderDate().getYear();
                // Note: For proper sorting, you usually use YYYY-MM as key, then format label later.
                // Simplified here:
            });

            // Better approach for sorting:
            Map<String, Double> monthlyMap = new TreeMap<>();
            for (LocalDate date = startDate.withDayOfMonth(1); !date.isAfter(endDate); date = date.plusMonths(1)) {
                monthlyMap.put(date.toString().substring(0, 7), 0.0); // Key: 2023-10
            }

            orders.forEach(o -> monthlyMap.merge(o.getOrderDate().toString().substring(0, 7), o.getTotalAmount(), Double::sum));

            monthlyMap.forEach((key, value) -> {
                LocalDate d = LocalDate.parse(key + "-01");
                chartData.add(new SalesReportDTO.SalesDataPoint(
                        d.getMonth().name().substring(0,3), // Label: JAN
                        value,
                        key
                ));
            });

        } else { // YEARLY
            Map<Integer, Double> yearlyMap = new TreeMap<>();
            for (int i = startDate.getYear(); i <= endDate.getYear(); i++) {
                yearlyMap.put(i, 0.0);
            }
            orders.forEach(o -> yearlyMap.merge(o.getOrderDate().getYear(), o.getTotalAmount(), Double::sum));

            yearlyMap.forEach((key, value) -> chartData.add(new SalesReportDTO.SalesDataPoint(
                    String.valueOf(key),
                    value,
                    String.valueOf(key)
            )));
        }

        return new SalesReportDTO(totalRevenue, totalCount, avgOrderValue, chartData);
    }

}
