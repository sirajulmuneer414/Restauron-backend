package dev.siraj.restauron.service.scheduledServices.reservationScheduleServices;

import dev.siraj.restauron.respository.reservation.reservationAvailability.dailyOverrideRepo.DailyOverrideRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

// Service class to clean up and other scheduled operation on reservation availabilities
@Slf4j
@Service
public class DailyOverrideCleanupService {

    @Autowired
    private DailyOverrideRepository overrideRepo;

    /**
     * Service to clean Up daily overrides that has expired
     */
    @Scheduled(cron = "0 1 12 * * ?")
    public void removeExpiredOverrides() {
        LocalDate today = LocalDate.now();

        log.info("Scheduled operation to clean up daily overrides that are expired running......");
        overrideRepo.deleteByDateLessThan(today.toString());
        log.info("clean up successfully completed");
    }
}
