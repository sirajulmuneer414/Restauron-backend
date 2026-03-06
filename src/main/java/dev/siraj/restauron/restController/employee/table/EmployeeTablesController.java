package dev.siraj.restauron.restController.employee.table;

import dev.siraj.restauron.DTO.employee.table.TableDetailResponseDTO;
import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employee.employeeTableService.EmployeeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/tables")
@RolesAllowed(roles = { "EMPLOYEE" })
public class EmployeeTablesController {

    private final EmployeeTableService employeeTableService;

    @Autowired
    public EmployeeTablesController(EmployeeTableService employeeTableService) {
        this.employeeTableService = employeeTableService;
    }

    /** Get all tables for this restaurant */
    @GetMapping()
    public ResponseEntity<List<RestaurantTableDTO>> getAllTables(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        return ResponseEntity.ok(employeeTableService.getAllTables(encryptedRestaurantId));
    }

    /** Get a single table with its active orders */
    @GetMapping("/{id}")
    public ResponseEntity<TableDetailResponseDTO> getTableDetail(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeTableService.getTableDetail(id, encryptedRestaurantId));
    }

    /**
     * Update the status of a specific table — fixed path (was /tables/{id}/status →
     * /{id}/status)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<RestaurantTableDTO> updateTableStatus(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable Long id,
            @RequestBody String statusDto) {

        return ResponseEntity.ok(employeeTableService.updateTableStatus(id, statusDto, encryptedRestaurantId));
    }
}
