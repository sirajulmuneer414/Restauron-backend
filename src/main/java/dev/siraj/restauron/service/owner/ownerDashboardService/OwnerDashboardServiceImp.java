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

        // Determine date range
        switch (type.toUpperCase()) {
            case "WEEKLY":
                startDate = endDate.minusWeeks(12);
                break;
            case "MONTHLY":
                startDate = endDate.minusMonths(12);
                break;
            case "YEARLY":
                startDate = endDate.minusYears(5);
                break;
            case "DAILY":
            default:
                startDate = endDate.minusDays(30);
                break;
        }

        List<Order> orders = orderRepository.findAllByRestaurantIdAndStatusAndOrderDateBetween(
                restaurantId,
                dev.siraj.restauron.entity.enums.OrderStatus.COMPLETED,
                startDate,
                endDate
        );

        double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
        long totalCount = orders.size();
        double avgOrderValue = totalCount > 0 ? totalRevenue / totalCount : 0.0;

        List<SalesReportDTO.SalesDataPoint> chartData = new ArrayList<>();

        if ("DAILY".equalsIgnoreCase(type)) {
            Map<String, Double> dailyMap = new TreeMap<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                dailyMap.put(date.toString(), 0.0);
            }
            orders.forEach(o -> dailyMap.merge(o.getOrderDate().toString(), o.getTotalAmount(), Double::sum));

            dailyMap.forEach((key, value) -> chartData.add(new SalesReportDTO.SalesDataPoint(
                    LocalDate.parse(key).getDayOfMonth() + "/" + LocalDate.parse(key).getMonthValue(),
                    value,
                    key
            )));

        } else if ("WEEKLY".equalsIgnoreCase(type)) {
            // FIX: Use TreeMap to ensure weeks are sorted chronologically
            Map<String, Double> weeklyMap = new TreeMap<>();

            // Initialize map with 0.0 for the range to ensure continuous graph
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusWeeks(1)) {
                int year = date.getYear();
                int week = date.get(weekFields.weekOfWeekBasedYear());
                // Key format: "2023-W05" to ensure proper string sorting
                String key = String.format("%d-W%02d", year, week);
                weeklyMap.put(key, 0.0);
            }

            orders.forEach(o -> {
                int year = o.getOrderDate().getYear();
                int week = o.getOrderDate().get(weekFields.weekOfWeekBasedYear());
                String key = String.format("%d-W%02d", year, week);
                // Only merge if key exists (within range) or put if you want strict data
                if (weeklyMap.containsKey(key)) {
                    weeklyMap.merge(key, o.getTotalAmount(), Double::sum);
                }
            });

            weeklyMap.forEach((key, value) -> chartData.add(new SalesReportDTO.SalesDataPoint(
                    key.substring(5), // Label: "W05"
                    value,
                    key
            )));

        } else if ("MONTHLY".equalsIgnoreCase(type)) {
            Map<String, Double> monthlyMap = new TreeMap<>();
            for (LocalDate date = startDate.withDayOfMonth(1); !date.isAfter(endDate); date = date.plusMonths(1)) {
                monthlyMap.put(date.toString().substring(0, 7), 0.0);
            }
            orders.forEach(o -> monthlyMap.merge(o.getOrderDate().toString().substring(0, 7), o.getTotalAmount(), Double::sum));

            monthlyMap.forEach((key, value) -> {
                LocalDate d = LocalDate.parse(key + "-01");
                chartData.add(new SalesReportDTO.SalesDataPoint(
                        d.getMonth().name().substring(0, 3),
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
