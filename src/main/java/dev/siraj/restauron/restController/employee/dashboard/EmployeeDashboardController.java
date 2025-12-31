package dev.siraj.restauron.restController.employee.dashboard;


import dev.siraj.restauron.DTO.employee.dashboard.EmployeeDashboardStatsDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.employee.employeeDashboard.EmployeeDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    // Controller for Employee Dashboard related endpoints

@RestController
@RequestMapping("/api/employee/dashboard")
@RolesAllowed(roles = {"EMPLOYEE"})
public class EmployeeDashboardController {

    private final EmployeeDashboardService employeeDashboardService;

    @Autowired
    public EmployeeDashboardController(EmployeeDashboardService employeeDashboardService) {
        this.employeeDashboardService = employeeDashboardService;
    }


    /**
     * Endpoint to retrieve dashboard statistics for an employee.
     *
     * @param encryptedRestaurantId The encrypted ID of the restaurant from request header.
     * @param encryptedEmployeeId The encrypted ID of the employee from request header.
     * @return ResponseEntity containing EmployeeDashboardStatsDTO with dashboard statistics.
     */
    @GetMapping("/stats")
    public ResponseEntity<EmployeeDashboardStatsDTO> getDashboardStats(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestHeader("X-Employee-Id") String encryptedEmployeeId
    ) {

        EmployeeDashboardStatsDTO stats = employeeDashboardService.getDashboardStats(encryptedEmployeeId, encryptedRestaurantId);

        return ResponseEntity.ok(stats);
    }

}
