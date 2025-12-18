package dev.siraj.restauron.service.reservation.reservationAvailability.weeklyAvailabilityService;

import dev.siraj.restauron.DTO.reservationAvailability.AvailabilitySettingRequestOwner;
import dev.siraj.restauron.entity.restaurant.management.reservation.WeeklyAvailability;
import dev.siraj.restauron.respository.reservation.reservationAvailability.weeklyAvailabiliyRepo.WeeklyAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeeklyAvailabilityServiceImp implements WeeklyAvailabilityService {

    @Autowired
    private WeeklyAvailabilityRepository weeklyRepo;

    /**
     *  This method is for saving weekly reservation availability of a restaurant by owner
     * @param restaurantId Encrypted Restaurant ID
     * @param week List of slots set up from frontend
     */
    @Transactional
    @Override
    public void saveWeeklyAvailability(String restaurantId, List<AvailabilitySettingRequestOwner> week) {
        weeklyRepo.deleteAllByRestaurantId(restaurantId); // Clear previous setup

        List<WeeklyAvailability> weeklyAvailibilityList = new ArrayList<>();

        for (AvailabilitySettingRequestOwner wa : week) {
            WeeklyAvailability wkAvail = new WeeklyAvailability();

            if(restaurantId.isEmpty() || restaurantId.isBlank()) throw new BadCredentialsException("RestaurantID is not entered");

            wkAvail.setRestaurantId(restaurantId);

            wkAvail.setSlots(wa.getSlots());

            wkAvail.setDayOfTheWeek(wa.getDay());

            weeklyAvailibilityList.add(wkAvail);

        }
        weeklyRepo.saveAll(weeklyAvailibilityList);
    }

    /**
     * This method is for getting the whole week's available slot for owner
     * @param restaurantId Encrypted Restaurant ID
     *
     * @return List Of Weekly availability data
     */
    @Override
    public List<WeeklyAvailability> getWeeklyAvailability(String restaurantId) {
        return weeklyRepo.findAllByRestaurantId(restaurantId);
    }


}



