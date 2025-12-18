package dev.siraj.restauron.restController.employee.table;

import dev.siraj.restauron.DTO.restaurant.restaurantTable.RestaurantTableDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.service.employee.employeeTableService.EmployeeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for Employee Table related endpoints

@RestController
@RequestMapping("/employee/tables")
@RolesAllowed(roles = {"EMPLOYEE"})
public class EmployeeTablesController {

    @Autowired
    private EmployeeTableService employeeTableService;

    /**
     * Endpoint to retrieve all tables for a restaurant.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant from request header.
     * @return ResponseEntity containing a list of RestaurantTable entities.
     */
    @GetMapping()
    public ResponseEntity<List<RestaurantTableDTO>> getAllTables(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ) {
        return ResponseEntity.ok(employeeTableService.getAllTables(encryptedRestaurantId));
    }

    /**
     * Endpoint to update the status of a specific table.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant from request header.
     * @param id The ID of the table to be updated.
     * @param statusDto The new status for the table.
     * @return ResponseEntity containing the updated RestaurantTable entity.
     */
    @PutMapping("/tables/{id}/status")
    public ResponseEntity<RestaurantTableDTO> updateTableStatus(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @PathVariable Long id,
            @RequestBody String statusDto) {

        return ResponseEntity.ok(employeeTableService.updateTableStatus(id, statusDto, encryptedRestaurantId));
    }


}
