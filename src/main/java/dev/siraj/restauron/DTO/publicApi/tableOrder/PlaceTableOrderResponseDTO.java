package dev.siraj.restauron.DTO.publicApi.tableOrder;

import dev.siraj.restauron.entity.enums.OrderStatus;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO after successfully placing a table order
 */
public record PlaceTableOrderResponseDTO(
        String billNumber,
        Double totalAmount,
        OrderStatus status,
        LocalDate orderDate,
        LocalTime orderTime,
        String message) {
}
