package dev.siraj.restauron.service.employee.employeeTableService;

import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import dev.siraj.restauron.entity.enums.table.TableStatus;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.repository.restaurantManagementRepos.RestaurantTableRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementation of Employee Table Service
@Service
public class EmployeeTableServiceImp implements EmployeeTableService {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;
    @Autowired
    private IdEncryptionService idEncryptionService;

    /**
     * This method retrieves all restaurant tables for a given encrypted restaurant ID.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant.
     * @return A list of RestaurantTable entities.
     */
    @Override
    public List<RestaurantTableDTO> getAllTables(String encryptedRestaurantId) {

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);

        return restaurantTableRepository.findByRestaurantId(restaurantId).stream().map(this::convertToDto).toList();
    }

    /**
     * This method updates the status of a restaurant table.
     *
     * @param id                    The ID of the restaurant table.
     * @param statusDto             The new status for the table.
     * @param encryptedRestaurantId The encrypted ID of the restaurant.
     * @return The updated RestaurantTable entity.
     */
    @Override
    public RestaurantTableDTO updateTableStatus(Long id, String statusDto, String encryptedRestaurantId) {
        RestaurantTable table = restaurantTableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Table not found"));

        table.setStatus(TableStatus.valueOf(statusDto.toUpperCase()));
        restaurantTableRepository.save(table);
        return convertToDto(table);
    }


// --------------------------------------------------------- HELPER METHODS --------------------------------------------------------- //

    private RestaurantTableDTO convertToDto(RestaurantTable table) {

        RestaurantTableDTO dto = new RestaurantTableDTO();
        dto.setTableId(table.getId());
        dto.setName(table.getName());
        dto.setCapacity(table.getCapacity());
        dto.setStatus(table.getStatus().name());
        return dto;
    }

}