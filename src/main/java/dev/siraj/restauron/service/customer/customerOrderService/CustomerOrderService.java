package dev.siraj.restauron.service.customer.customerOrderService;


import dev.siraj.restauron.DTO.customer.orders.CustomerOrderDetailDTO;
import dev.siraj.restauron.DTO.customer.orders.CustomerOrderSummaryDTO;
import dev.siraj.restauron.DTO.customer.rating.MenuItemRatingStatsDTO;
import dev.siraj.restauron.DTO.customer.rating.RatingResponseDTO;
import dev.siraj.restauron.DTO.customer.rating.SubmitRatingDTO;

import java.util.List;

public interface CustomerOrderService {

    // Get customer's order history
    List<CustomerOrderSummaryDTO> getCustomerOrders(Long customerId, String status);

    // Get order details
    CustomerOrderDetailDTO getOrderDetails(String encryptedOrderId, Long customerId);

    // Submit rating for a menu item
    RatingResponseDTO submitRating(String encryptedOrderId, String encryptedMenuItemId,
                                   SubmitRatingDTO ratingDTO, Long customerId);

    // Get ratings for a menu item
    List<RatingResponseDTO> getMenuItemRatings(String encryptedMenuItemId);

    // Get rating stats for a menu item
    MenuItemRatingStatsDTO getMenuItemRatingStats(String encryptedMenuItemId);
}