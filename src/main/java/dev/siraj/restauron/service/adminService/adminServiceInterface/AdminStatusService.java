package dev.siraj.restauron.service.adminService.adminServiceInterface;

    // Interface for admin statistics service

import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;

import java.util.List;
import java.util.Map;

public interface AdminStatusService {
    // Method to count total number of restaurants
    long countRestaurants();

    // Method to count total number of customers
    long countCustomers();

    // Method to count active reservations for today
    long countActiveReservations();

    // Method to get summary data of the last five created restaurants
    List<RestaurantSummaryDTO> getRestaurantTableData();

    // Method to get reservation statistics over time
    List<Map<String, Object>> getReservationsOverTime();

    // Method to get reservation status split data
    List<Map<String, Object>> getReservationStatusSplit();
}
