package dev.siraj.restauron.service.employee.employeeTableService;

import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;

import java.util.List;

// This is interface for Employee Table Service
public interface EmployeeTableService {
        List<RestaurantTableDTO> getAllTables(String encryptedRestaurantId);

    RestaurantTableDTO updateTableStatus(Long id, String statusDto, String encryptedRestaurantId);
}
