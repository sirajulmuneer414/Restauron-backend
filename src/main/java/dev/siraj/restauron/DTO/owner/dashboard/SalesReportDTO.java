package dev.siraj.restauron.DTO.owner.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDTO {
    private Double totalRevenue;
    private Long totalOrders;
    private Double averageOrderValue;
    private List<SalesDataPoint> chartData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SalesDataPoint {
        private String label; // e.g., "Mon", "Jan", "2024"
        private Double value; // Revenue amount
        private String date;  // ISO date string for sorting/tooltips
    }
}