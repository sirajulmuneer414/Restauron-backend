package dev.siraj.restauron.restController.publicApi;

import dev.siraj.restauron.DTO.publicApi.tableOrder.PlaceTableOrderRequestDTO;
import dev.siraj.restauron.DTO.publicApi.tableOrder.PlaceTableOrderResponseDTO;
import dev.siraj.restauron.DTO.publicApi.tableOrder.TableOrderInfoResponseDTO;
import dev.siraj.restauron.service.publicApi.tableOrder.interfaces.TableOrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public REST controller for table-side ordering
 * No authentication required - customers access via QR code
 */
@RestController
@RequestMapping("/api/public/table-order")
@Slf4j
public class PublicTableOrderController {

    private final TableOrderService tableOrderService;

    @Autowired
    public PublicTableOrderController(TableOrderService tableOrderService) {
        this.tableOrderService = tableOrderService;
    }

    /**
     * Get table information and available menu items
     * 
     * @param encryptedTableId Encrypted table ID from QR code
     * @return Table info with restaurant details and menu categories
     */
    @GetMapping("/{encryptedTableId}")
    public ResponseEntity<?> getTableOrderInfo(@PathVariable String encryptedTableId) {
        try {
            log.info("GET /api/public/table-order/{} - Fetching table and menu info", encryptedTableId);
            TableOrderInfoResponseDTO response = tableOrderService.getTableOrderInfo(encryptedTableId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid table request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching table order info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unable to load menu. Please try again."));
        }
    }

    /**
     * Place an order from a table
     * 
     * @param encryptedTableId Encrypted table ID
     * @param request          Order request with customer details and items
     * @return Order confirmation
     */
    @PostMapping("/{encryptedTableId}/place-order")
    public ResponseEntity<?> placeTableOrder(
            @PathVariable String encryptedTableId,
            @Valid @RequestBody PlaceTableOrderRequestDTO request) {
        try {
            log.info("POST /api/public/table-order/{}/place-order - Placing order for customer: {}",
                    encryptedTableId, request.customerName());
            PlaceTableOrderResponseDTO response = tableOrderService.placeTableOrder(encryptedTableId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid order placement: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error placing table order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unable to place order. Please try again or contact staff."));
        }
    }

    /**
     * Simple error response record
     */
    private record ErrorResponse(String error) {
    }
}
