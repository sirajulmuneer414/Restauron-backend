package dev.siraj.restauron.service.employee.employeeDashboard;

import dev.siraj.restauron.DTO.employee.dashboard.EmployeeDashboardStatsDTO;
import org.springframework.stereotype.Service;

    // Implementation of Employee Dashboard Service

@Service
public class EmployeeDashboardServiceImp implements EmployeeDashboardService{

    /**
     * This method retrieves dashboard statistics for an employee based on their encrypted ID and the restaurant's encrypted ID.
     *
     * @param encryptedEmployeeId The encrypted ID of the employee.
     * @param encryptedRestaurantId The encrypted ID of the restaurant.
     * @return An EmployeeDashboardStatsDTO containing the dashboard statistics.
     */
    @Override
    public EmployeeDashboardStatsDTO getDashboardStats(String encryptedEmployeeId, String encryptedRestaurantId) {
        return null;
    }
}
