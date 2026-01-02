package dev.siraj.restauron.service.owner.ownerDashboardService;

import dev.siraj.restauron.DTO.owner.customerSideUrl.RestaurantLinkDTO;
import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSalesStatsDTO;
import dev.siraj.restauron.DTO.owner.dashboard.OwnerDashboardSubscriptionDTO;
import dev.siraj.restauron.DTO.owner.dashboard.TopItemDTO;
import dev.siraj.restauron.DTO.owner.orderManagement.OrderDetailDto;

import java.util.List;

// Service interface for OwnerDashboardService
public interface OwnerDashboardService {
    OwnerDashboardSalesStatsDTO getSalesStats(Long restaurantId);

    OwnerDashboardSubscriptionDTO getSubscriptionStatus(Long restaurantId);

    List<TopItemDTO> getTopSellingItems(Long restaurantId);

    long getEmployeeCount(Long restaurantId);

    List<OrderDetailDto> findRecentOrders(long restaurantId);

    RestaurantLinkDTO getRestaurantCustomerLink(String encryptedRestaurantId);
}
