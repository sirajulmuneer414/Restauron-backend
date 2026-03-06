package dev.siraj.restauron.service.employee.employeeTableService;

import dev.siraj.restauron.DTO.employee.table.TableDetailResponseDTO;
import dev.siraj.restauron.DTO.employee.table.TableOrderSummaryDTO;
import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import dev.siraj.restauron.entity.enums.table.TableStatus;
import dev.siraj.restauron.entity.orderManagement.Order;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.repository.orderRepo.OrderRepository;
import dev.siraj.restauron.repository.restaurantManagementRepos.RestaurantTableRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeTableServiceImp implements EmployeeTableService {

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private IdEncryptionService idEncryptionService;

    @Override
    public List<RestaurantTableDTO> getAllTables(String encryptedRestaurantId) {
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        return restaurantTableRepository.findByRestaurantId(restaurantId).stream().map(this::convertToDto).toList();
    }

    @Override
    public RestaurantTableDTO updateTableStatus(Long id, String statusDto, String encryptedRestaurantId) {
        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));
        table.setStatus(TableStatus.valueOf(statusDto.toUpperCase()));
        restaurantTableRepository.save(table);
        return convertToDto(table);
    }

    @Override
    public TableDetailResponseDTO getTableDetail(Long tableId, String encryptedRestaurantId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));

        List<Order> orders = orderRepository.findByRestaurantTableId(tableId);

        List<TableOrderSummaryDTO> orderSummaries = orders.stream().map(order -> {
            String customerName;
            if (order.getCustomer() != null) {
                customerName = order.getCustomer().getUser().getName();
            } else if (order.getTemporaryCustomerName() != null) {
                customerName = order.getTemporaryCustomerName();
            } else {
                customerName = "Walk-in";
            }
            return new TableOrderSummaryDTO(
                    order.getId(),
                    idEncryptionService.encryptLongId(order.getId()),
                    order.getBillNumber(),
                    customerName,
                    order.getStatus().name());
        }).toList();

        return new TableDetailResponseDTO(
                table.getId(),
                table.getName(),
                table.getStatus().name(),
                table.getCapacity(),
                orderSummaries);
    }

    // ---- Helper ----
    private RestaurantTableDTO convertToDto(RestaurantTable table) {
        RestaurantTableDTO dto = new RestaurantTableDTO();
        dto.setTableId(table.getId());
        dto.setName(table.getName());
        dto.setCapacity(table.getCapacity());
        dto.setStatus(table.getStatus().name());
        return dto;
    }
}
