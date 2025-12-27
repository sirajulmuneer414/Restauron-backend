package dev.siraj.restauron.restController.admin;

import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


// Controller class for admin statistics operations

@RestController
@RolesAllowed(roles = {"ADMIN"})
@RequestMapping("/admin/stats")
public class AdminStatsController {

    private static final Logger log = LoggerFactory.getLogger(AdminStatsController.class);
    @Autowired
    private AdminStatusService adminStatsService;


    /**
     * Controller method to get total number of restaurants
     * @return ResponseEntity with total restaurants count
     */
    @GetMapping("/total-restaurants")
    public ResponseEntity<Map<String, Long>> getTotalRestaurants() {
        long count = adminStatsService.countRestaurants();
        return ResponseEntity.ok(Map.of("totalRestaurants", count));
    }

    /**
     * Controller method to get total number of customers
     * @return ResponseEntity with total customers count
     */
    @GetMapping("/total-customers")
    public ResponseEntity<Map<String, Long>> getTotalCustomers() {
        long count = adminStatsService.countCustomers();
        return ResponseEntity.ok(Map.of("totalCustomers", count));
    }

    /**
     * Controller method to get total number of active reservations for today
     * @return ResponseEntity with total active reservations count
     */
    @GetMapping("/active-reservations")
    public ResponseEntity<Map<String,Long>> getActiveReservations() {
        long count = adminStatsService.countActiveReservations();
        return ResponseEntity.ok(Map.of("totalReservations", count));
    }

    /**
     * Controller method to get summary data of the last five created restaurants
     * @return ResponseEntity with list of RestaurantSummaryDTO
     */
    @GetMapping("/restaurants/list")
    public ResponseEntity<List<RestaurantSummaryDTO>> getRestaurantsListOfLastFive() {
        List<RestaurantSummaryDTO> restaurantList = adminStatsService.getRestaurantTableData();
        return ResponseEntity.ok(restaurantList);
    }


    /**
     * Controller method to get reservation statistics over time
     * @return ResponseEntity with list of reservation statistics {date, reservationCount}
     */
    @GetMapping("/reservations-over-time")
    public ResponseEntity<List<Map<String, Object>>> getReservationsOverTime() {
        List<Map<String, Object>> data = adminStatsService.getReservationsOverTime();
        return ResponseEntity.ok(data);
    }

    /**
     * Controller method to get reservation status split data
     * @return ResponseEntity with list of reservation status split {status, count}
     */
    @GetMapping("/reservation-status-split")
    public ResponseEntity<List<Map<String, Object>>> getReservationStatusSplit() {
        log.info("Fetching reservation status split data");
        List<Map<String, Object>> data = adminStatsService.getReservationStatusSplit();
        log.info("Fetched reservation status split data: {}", data);
        return ResponseEntity.ok(data);
    }
}
