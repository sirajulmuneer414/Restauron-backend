package dev.siraj.restauron.service.employee.employeeTableService;

import dev.siraj.restauron.DTO.employee.table.TableDetailResponseDTO;
import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;

import java.util.List;

public interface EmployeeTableService {
    List<RestaurantTableDTO> getAllTables(String encryptedRestaurantId);

    RestaurantTableDTO updateTableStatus(Long id, String statusDto, String encryptedRestaurantId);

    TableDetailResponseDTO getTableDetail(Long tableId, String encryptedRestaurantId);
}
