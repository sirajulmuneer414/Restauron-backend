package dev.siraj.restauron.restController.owner;

import dev.siraj.restauron.DTO.reservationAvailability.AvailabilitySettingRequestOwner;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import dev.siraj.restauron.service.reservation.reservationAvailability.dailyOverrideService.DailyOverrideService;
import dev.siraj.restauron.service.reservation.reservationAvailability.weeklyAvailabilityService.WeeklyAvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/owner/reservation-availability")
@RolesAllowed(roles = {"OWNER"})
public class OwnerReservationAvailabilityController {


    @Autowired
    private WeeklyAvailabilityService weeklyAvailabilityService;
    @Autowired
    private DailyOverrideService dailyOverrideService;

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
