package dev.siraj.restauron.DTO.orders;

import lombok.Data;

@Data
public class OrderPageRequestDto {

    private int page = 0; // Default to page 0
    private int size = 10; // Default to size 10

    // Corresponds to filterStatus
    private String status;

    // Corresponds to filterType
    private String type;

    private String search;

    // Corresponds to sort: 'orderDate,desc'
    private String sort;
}
