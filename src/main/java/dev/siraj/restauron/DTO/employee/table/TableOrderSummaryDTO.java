package dev.siraj.restauron.DTO.employee.table;

public record TableOrderSummaryDTO(
                Long orderId,
                String encryptedOrderId,
                String billNumber,
                String customerName,
                String status) {
}
