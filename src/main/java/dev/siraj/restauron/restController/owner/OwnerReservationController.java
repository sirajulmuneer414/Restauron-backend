package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.reservations.ReservationDto;
import dev.siraj.restauron.entity.enums.ReservationStatus;
import dev.siraj.restauron.entity.restaurant.management.reservation.Reservation;
import dev.siraj.restauron.service.reservation.reservationService.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// Controller class for reservation operations from owner side
@Slf4j
@RestController
@RequestMapping("/owner/reservations")
public class OwnerReservationController {

    @Autowired
    private ReservationService reservationService;


    /**
     * Controller method for fetching reservation with pagination, status filter and search filter
     *
     * @param encryptedRestaurantId Encrypted Restaurant ID - from OWNER side
     * @param page No of Page
     * @param size Size of Page
     * @param status Status Enum filter
     * @param search Search filter
     *
     * @return Page of Reservation
     *
     */
    @GetMapping()
    public ResponseEntity<?> getAllReservations(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ){

        log.info("Inside the controller to fetch page reservations with filter, Search and Pagination for restaurant {}", encryptedRestaurantId);

        ReservationStatus statusEnum = null;
        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            statusEnum = ReservationStatus.valueOf(status.toUpperCase());
        }

        Page<Reservation> reservationPage = reservationService.findAllPagesWithFilterAndSearch(page, size,encryptedRestaurantId, statusEnum, search);

        log.info("Successfully fetched the page of reservations");

        return new ResponseEntity<>(reservationPage, HttpStatus.OK);
    }

    /**
     *  Controller Method for adding reservation - from OWNER side
     *
     * @param encryptedRestaurantId Encrypted Restaurant ID from OWNER SIDE
     * @param request Reservation DTO Object
     *
     * @return HttpStatus Code OK
     */
    @PostMapping
    public ResponseEntity<?> addReservation(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody ReservationDto request) {

        System.out.println(request.getReservationDate());
        System.out.println(request.getReservationTime());
        System.out.println(request.getCustomerName());
        System.out.println(
                request.getCustomerEmail()
        );

        log.info("Inside the controller to add a reservation from OWNER side");
        reservationService.addReservation(encryptedRestaurantId, request, "OWNER");
        log.info("Successfully added reservation");
        return ResponseEntity.ok().build();
    }

}
