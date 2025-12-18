package dev.siraj.restauron.DTO.owner.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopItemDTO {
    private Long itemId;
    private String name;
    private Long count;
    private Double revenue;
}