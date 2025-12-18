package dev.siraj.restauron.service.employee.employeeDashboard;

import dev.siraj.restauron.DTO.employee.dashboard.EmployeeDashboardStatsDTO;

// This is interface for Employee Dashboard Service
public interface EmployeeDashboardService {
        EmployeeDashboardStatsDTO getDashboardStats(String encryptedEmployeeId, String encryptedRestaurantId);
    }
