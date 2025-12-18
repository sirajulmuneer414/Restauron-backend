package dev.siraj.restauron.service.scheduledServices.subscriptionSchedulersServices;


import dev.siraj.restauron.entity.subscription.RestaurantSubscription;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import dev.siraj.restauron.respository.subscription.RestaurantSubscriptionRepository;
import dev.siraj.restauron.respository.subscription.SubscriptionPackageRepository;
import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

    // Scheduler service to check and update restaurant subscription statuses daily

@Slf4j
@Component
public class SubscriptionScheduler {

    @Autowired
    private RestaurantSubscriptionRepository subscriptionRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepository;


    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkSubscriptionStatus() {
        LocalDate today = LocalDate.now();

        // 1. Handle Expiry
        List<RestaurantSubscription> expiringToday = subscriptionRepo.findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, today);
        for (RestaurantSubscription sub : expiringToday) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            emailService.sendSubscriptionExpiredEmail(sub.getRestaurant().getEmail(), sub.getRestaurant().getName());
            // Don't delete! Just mark as EXPIRED.
        }
        subscriptionRepo.saveAll(expiringToday);

        // 2. Handle Warnings (5 days left)
        List<RestaurantSubscription> fiveDaysLeft = subscriptionRepo.findExpiringSubscriptions(today.plusDays(5), false);
        for (RestaurantSubscription sub : fiveDaysLeft) {
            emailService.sendWarningEmail(sub.getRestaurant().getEmail(),sub.getRestaurant().getName() , 5);
            sub.setReminder5DaysSent(true);
        }
        subscriptionRepo.saveAll(fiveDaysLeft);

        // Repeat logic for 2 days

        List<RestaurantSubscription> twoDaysLeft = subscriptionRepo.findExpiringSubscriptions(today.plusDays(2), false);
        for (RestaurantSubscription sub : twoDaysLeft) {
            emailService.sendWarningEmail(sub.getRestaurant().getEmail(),sub.getRestaurant().getName() , 2);
            sub.setReminder2DaysSent(true);
        }
        subscriptionRepo.saveAll(twoDaysLeft);

        // Repeat logic for 1 day
        List<RestaurantSubscription> oneDayLeft = subscriptionRepo.findExpiringSubscriptions(today.plusDays(1), false);
        for (RestaurantSubscription sub : oneDayLeft) {
            emailService.sendWarningEmail(sub.getRestaurant().getEmail(),sub.getRestaurant().getName() , 1);
            sub.setReminder1DaySent(true);
        }
        subscriptionRepo.saveAll(oneDayLeft);

        // 3. Handle Expired Offers in Subscription Packages
        List<SubscriptionPackage> expiredOffers = subscriptionPackageRepository.findPackagesWithExpiredOffers(today);
        for (SubscriptionPackage pack : expiredOffers) {
            log.info("Removing expired offer from package: {}", pack.getName());
            pack.setOffer(null); // Remove expired offer
        }

        if(!expiredOffers.isEmpty()){
            subscriptionPackageRepository.saveAll(expiredOffers);
        }
    }

}
