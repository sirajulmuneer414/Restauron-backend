package dev.siraj.restauron.service.reservation.reservationAvailability.availabiliyCheckingService;


import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import dev.siraj.restauron.entity.restaurant.management.reservation.Slots;
import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import dev.siraj.restauron.respository.reservation.reservationAvailability.dailyOverrideRepo.DailyOverrideRepository;
import dev.siraj.restauron.respository.reservation.reservationAvailability.weeklyAvailabiliyRepo.WeeklyAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


// SERVICE TO CHECK THE AVAILABILITY FOR RESERVATION

@Service
public class AvailabilityCheckingServiceImp implements AvailabilityCheckingService{

    @Autowired
    private DailyOverrideRepository dailyOverrideRepo;
    @Autowired
    private WeeklyAvailabilityRepository weeklyAvailabilityRepo;


    @Override
    public List<Slots> getSlotsForTheDate(String date, String encryptedRestaurantId) {

        DailyOverride dailyOverride = dailyOverrideRepo.findByRestaurantIdAndDate(encryptedRestaurantId, date).orElse(null);

        if(dailyOverride != null) {
            List<Slots> slotsAvailableFromDailyOverride = dailyOverride.getSlots();

            slotsAvailableFromDailyOverride.forEach(System.out::println);

            if(!slotsAvailableFromDailyOverride.isEmpty()) return slotsAvailableFromDailyOverride;
        }

        String dayOfWeek = getDayOfTheWeek(date);

        WeeklyAvailability weeklyAvailability = weeklyAvailabilityRepo.findByRestaurantIdAndDayOfTheWeek(encryptedRestaurantId, dayOfWeek).orElse(null);

        if(weeklyAvailability != null){
            List<Slots> slotsAvailableFromWeeklyAvailability = weeklyAvailability.getSlots();

            if(!slotsAvailableFromWeeklyAvailability.isEmpty()) return slotsAvailableFromWeeklyAvailability;
        }

        return new ArrayList<>();

    }



    // ------------------------------------------------- HELPER METHODS --------------------------------------------------------

    /**
     * To get the day of the week for a given date
     *
     * @param dateString Date in String format
     * @return DayOfTheWeek enum in normal English format
     */
    private String getDayOfTheWeek(String dateString){

        LocalDate date = LocalDate.parse(dateString);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
