package dev.siraj.restauron.restController.owner;


import dev.siraj.restauron.DTO.admin.RestaurantUpdateDto;
import dev.siraj.restauron.DTO.owner.restaurantManagement.TableResponseDto;
import dev.siraj.restauron.DTO.table.RestaurantTableRequestDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.owner.ownerService.interfaces.OwnerService;
import dev.siraj.restauron.service.owner.ownerService.interfaces.OwnerTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/tables")
@RolesAllowed(roles = {"OWNER"})
@Slf4j
public class OwnerTableController {

    private final OwnerTableService tableService;
    private final OwnerService ownerService;

    @Autowired
    public OwnerTableController(OwnerTableService tableService, OwnerService ownerService) {
        this.tableService = tableService;
        this.ownerService = ownerService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createTable(@RequestBody RestaurantTableRequestDTO payload, @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("inside the controller to create table");
        // Assuming your UserDetails implementation has a method to get the user ID
        Long ownerId = ownerService.getOwnerIdFromRestaurantEncryptedId(encryptedRestaurantId);
        tableService.createTable(payload.name(), payload.capacity() , ownerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<TableResponseDto>> listTables(@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {

        log.info("Inside the controller to fetch list of tables");
        Long ownerId = ownerService.getOwnerIdFromRestaurantEncryptedId(encryptedRestaurantId);

        return ResponseEntity.ok(tableService.getTablesForOwner(ownerId));
    }

    @DeleteMapping("/delete/{encryptedTableId}")
    public ResponseEntity<?> deleteTable(@PathVariable String encryptedTableId,@RequestHeader("X-Restaurant-Id") String encryptedRestaurantId) {
        log.info("Inside the delete table controller with : restaurantId {} and table id {}",encryptedRestaurantId,encryptedTableId);
        Long ownerId = ownerService.getOwnerIdFromRestaurantEncryptedId(encryptedRestaurantId);
        log.info("Successfully got ownerId {}", ownerId);
        try {
            tableService.deleteTable(encryptedTableId, ownerId);
            log.info("Deleted table ");
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Your are not authorized to access this", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.noContent().build();
    }
}
