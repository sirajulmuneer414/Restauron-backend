package dev.siraj.restauron.service.publicApi.tableOrder.interfaces;

import dev.siraj.restauron.DTO.publicApi.tableOrder.PlaceTableOrderRequestDTO;
import dev.siraj.restauron.DTO.publicApi.tableOrder.PlaceTableOrderResponseDTO;
import dev.siraj.restauron.DTO.publicApi.tableOrder.TableOrderInfoResponseDTO;

/**
 * Service interface for public table-side ordering functionality
 */
public interface TableOrderService {

    /**
     * Get table information and available menu items for customer ordering
     *
     * @param encryptedTableId Encrypted table ID from QR code URL
     * @return Table info with restaurant details and available menu items grouped
     *         by category
     * @throws IllegalArgumentException if table ID is invalid or table is not
     *                                  available
     */
    TableOrderInfoResponseDTO getTableOrderInfo(String encryptedTableId);

    /**
     * Place an order from a table
     *
     * @param encryptedTableId Encrypted table ID
     * @param request          Order request containing customer details and items
     * @return Order confirmation details
     * @throws IllegalArgumentException if table is not available or menu items are
     *                                  invalid
     */
    PlaceTableOrderResponseDTO placeTableOrder(String encryptedTableId, PlaceTableOrderRequestDTO request);
}
