package dev.siraj.restauron.DTO.owner.dashboard;


import lombok.Data;

@Data
public class OwnerDashboardSalesStatsDTO {
    private double today;
    private double week;
    private double month;
    private double year;
}