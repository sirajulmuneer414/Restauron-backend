package dev.siraj.restauron.service.adminService;

import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;
import dev.siraj.restauron.entity.enums.ReservationStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.respository.customerRepo.CustomerRepository;
import dev.siraj.restauron.respository.reservation.reservationRepo.ReservationRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.adminService.adminServiceInterface.AdminStatusService;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

// Implementation of AdminStatusService interface

@Service
public class AdminStatusServiceImp implements AdminStatusService {

    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private IdEncryptionService idEncryptionService;

    /**
     * Method to count total number of restaurants
     * @return total restaurant count
     */
    @Override
    public long countRestaurants() {

        return restaurantRepository.count();

    }

    /**
     * Method to count total number of customers
     * @return total customer count
     */
    @Override
    public long countCustomers() {
        return customerRepository.count();
    }

    /**
     * Method to count active reservations for today
     * @return total active reservations count for today
     */
    @Override
    public long countActiveReservations() {

        String today = LocalDate.now().toString();
        return reservationRepository.countByReservationDateAndCurrentStatus(today, ReservationStatus.CONFIRMED);
    }

    /**
     * Method to get summary data of the last five created restaurants
     * @return list of RestaurantSummaryDTO for the last five restaurants
     */
    @Override
    public List<RestaurantSummaryDTO> getRestaurantTableData() {
        List<Restaurant> lastFive = restaurantRepository.findTop5ByOrderByCreatedAtDesc();


        return lastFive.stream().map(r -> {
            long customerCount = customerRepository.countByRestaurantId(r.getId());

            RestaurantSummaryDTO dto = new RestaurantSummaryDTO();
                dto.setName(r.getName());
                dto.setCreatedAt(r.getCreatedAt().toString());
                dto.setStatus(r.getStatus().toString());
                dto.setCustomerCount(customerCount);
                dto.setRestaurantEncryptedId(idEncryptionService.encryptLongId(r.getId()));

            return dto;
        }).toList();
    }

    /**
     * Method to get reservation statistics over the past 14 days
     * @return list of maps containing date and reservation count
     */
    @Override
    public List<Map<String, Object>> getReservationsOverTime() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(13);

        // Returns List<Object[]> {date, count}
        List<Object[]> raw = reservationRepository.countReservationsByDateBetween(start.toString(), end.toString());

        // Map results to {date: ..., reservations: ...}
        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> foundDates = new HashSet<>();
        for (Object[] row : raw) {
            String date = (String) row[0];
            Long count = (Long) row[1];
            results.add(Map.of("date", date, "reservations", count));
            foundDates.add(date);
        }
        // Fill missing dates with 0
        for (int i = 0; i <= 13; i++) {
            String date = start.plusDays(i).toString();
            if (!foundDates.contains(date)) {
                results.add(Map.of("date", date, "reservations", 0));
            }
        }
        // Sort by date ascending
        results.sort(Comparator.comparing(m -> (String) m.get("date")));
        return results;
    }

    /**
     * Method to get reservation status split data
     * @return list of maps containing reservation status and count
     */
    @Override
    public List<Map<String, Object>> getReservationStatusSplit() {

        // Returns e.g. [{status: "CONFIRMED", count: 44}, ...]
        List<Object[]> raw = reservationRepository.countReservationsByStatus();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (Object[] row : raw) {
            ReservationStatus status = (ReservationStatus) row[0];
            Long count = (Long) row[1];
            chartData.add(Map.of("name", status.name(), "value", count));
        }
        return chartData;
    }


}
