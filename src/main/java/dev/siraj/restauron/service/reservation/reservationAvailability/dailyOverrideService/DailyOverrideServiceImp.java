package dev.siraj.restauron.service.reservation.reservationAvailability.dailyOverrideService;

import dev.siraj.restauron.DTO.reservationAvailability.AvailabilitySettingRequestOwner;
import dev.siraj.restauron.entity.restaurant.management.reservation.DailyOverride;
import dev.siraj.restauron.repository.reservation.reservationAvailability.dailyOverrideRepo.DailyOverrideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DailyOverrideServiceImp implements DailyOverrideService{

    @Autowired private DailyOverrideRepository overrideRepo;


    /**
     * Service to add overrides availability on a daily basis
     *
     * @param restaurantId Encrypted Restaurant ID - used to fetch the overrides from my db
     * @param overridesList List of AvailabilitySettingRequestOwner - Object that has the slots and date (day) for the entry
     */
    @Transactional
    @Override
    public void saveOverrides(String restaurantId, List<AvailabilitySettingRequestOwner> overridesList) {


        overrideRepo.deleteAllByRestaurantId(restaurantId);


        List<DailyOverride> newOverrides = new ArrayList<>();

        for ( AvailabilitySettingRequestOwner overridesFromList : overridesList) {
            DailyOverride override = new DailyOverride();
            override.setRestaurantId(restaurantId);
            override.setDate(overridesFromList.getDay());
            override.setSlots(overridesFromList.getSlots());
            newOverrides.add(override);
        }

        overrideRepo.saveAll(newOverrides);
    }


    /**
     * Service to fetch all the overrides present for a particular restaurant
     * @param restaurantId Encrypted Restaurant ID -  from owner - used to fetch according to the encrypted ID saved
     *
     * @return List of Daily Override
     */
    @Override
    public List<DailyOverride> getOverrides(String restaurantId) {

        return overrideRepo.findAllByRestaurantId(restaurantId);
    }
}
