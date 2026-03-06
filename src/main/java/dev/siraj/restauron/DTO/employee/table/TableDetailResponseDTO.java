package dev.siraj.restauron.DTO.employee.table;

import java.util.List;

public record TableDetailResponseDTO(
        Long tableId,
        String name,
        String status,
        Integer capacity,
        List<TableOrderSummaryDTO> orders) {
}
