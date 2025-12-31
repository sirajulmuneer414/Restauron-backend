package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.reservationAvailability.AvailabilitySettingRequestOwner;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import dev.siraj.restauron.entity.restaurant.management.reservation.Slots;
import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import dev.siraj.restauron.service.reservation.reservationAvailability.availabiliyCheckingService.AvailabilityCheckingService;
import dev.siraj.restauron.service.reservation.reservationAvailability.dailyOverrideService.DailyOverrideService;
import dev.siraj.restauron.service.reservation.reservationAvailability.weeklyAvailabilityService.WeeklyAvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/owner/reservation-availability")
@RolesAllowed(roles = {"OWNER"})
public class OwnerReservationAvailabilityController {


    private final AvailabilityCheckingService availabilityCheckingService;

    private final WeeklyAvailabilityService weeklyAvailabilityService;

    private final DailyOverrideService dailyOverrideService;


    @Autowired
    public OwnerReservationAvailabilityController(
            AvailabilityCheckingService availabilityCheckingService,
            WeeklyAvailabilityService weeklyAvailabilityService,
            DailyOverrideService dailyOverrideService
    ) {
        this.availabilityCheckingService = availabilityCheckingService;
        this.weeklyAvailabilityService = weeklyAvailabilityService;
        this.dailyOverrideService = dailyOverrideService;
    }

    // ------------------------------------------- AVAILABILITY CHECKING --------------------------------------------------

    /**
     * Controller Method to fetch the slots available for a particular date
     *
     * @param encryptedRestaurantId Encrypted restaurant ID
     * @param date Date that need to be checked for slot - "YYYY-MM-DD" format
     *
     * @return List of Slots with noOfSlots and noOfPeople that is max
     */
    @GetMapping("/getSlots")
    public ResponseEntity<?> getAvailableSlotsForTheDay(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestParam String date
    ){
        log.info("Inside the controller to fetch available slots for a date {}", date);

        List<Slots> slots = availabilityCheckingService.getSlotsForTheDate(date, encryptedRestaurantId);

        if (slots.isEmpty()) return new ResponseEntity<>("No Slots available for this date", HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(slots);

    }


    // ------------------------------------------- WEEKLY AVAILABILITY ----------------------------------------------------

    /**
     *  Controller to fetch all weekly availability of a restaurant
     * @param encryptedRestaurantId Encrypted Restaurant ID
     *
     * @return return list of weekly availability
     */
    @GetMapping("/weekly")
    public ResponseEntity<?> getAllWeeklyReservationAvailability(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ){

        log.info("Inside the reservation availability list fetch - weekly");

        List<WeeklyAvailability> listsAvailable = weeklyAvailabilityService.getWeeklyAvailability(encryptedRestaurantId);

        log.info("Successfully fetched the list of reservation availability data");


        return ResponseEntity.ok(listsAvailable);
    }


    /**
     *  Controller to add weekly availability data
     *
     * @param encryptedRestaurantId Encrypted Restaurant ID
     * @param week List of Weekly availability DTO from owner Side
     *
     * @return Response Entity with the location that is current itself
     */
    @PostMapping("/weekly")
    public ResponseEntity<?> setWeeklyAvailabilityForEveryDayOfWeek(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody List<AvailabilitySettingRequestOwner> week
    ){
        log.info("Inside controller to set weekly availability by owner");

        weeklyAvailabilityService.saveWeeklyAvailability(encryptedRestaurantId, week);

        log.info("Successfully added the weekly availability");

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        return ResponseEntity.created(location).build();
    }

    // --------------------------------------------------------- DAILY OVERRIDES ------------------------------------------

    /**
     * Controller to fetch the daily overrides in relation to the encrypted Restaurant ID - from owner
     *
     * @param encryptedRestaurantId Encrypted Restaurant ID - from owner side
     *
     * @return List of daily overrides ( date, slots, createdBy etc.)
     */
    @GetMapping("/override")
    public ResponseEntity<?> getAllDailyOverrideAvailabilityForOwner(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ){
        log.info("Inside the controller to fetch all daily override availability - for owner");

        List<DailyOverride> dailyOverrides = dailyOverrideService.getOverrides(encryptedRestaurantId);

        log.info("Successfully fetched all daily overrides for the restaurant with encrypted ID {}",encryptedRestaurantId);

        return ResponseEntity.ok(dailyOverrides);
    }


    /**
     * Controller for creating daily overrides
     * @param encryptedRestaurantId Encrypted Restaurant ID - From Owner Side
     * @param availabilityList List of availability setting request ( day, List<Slots> )
     *
     * @return return created() responseEntity with current location URI
     */
    @PostMapping("/override")
    public ResponseEntity<?> setDailyOverridesFromOwner(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId,
            @RequestBody List<AvailabilitySettingRequestOwner> availabilityList
    ){
        log.info("Inside the controller to setup daily overrides from owner for the restaurant {}", encryptedRestaurantId);

        dailyOverrideService.saveOverrides(encryptedRestaurantId,availabilityList);

        log.info("Successfully created daily overrides for the restaurant {}", encryptedRestaurantId);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        return ResponseEntity.created(location).build();
    }
}
