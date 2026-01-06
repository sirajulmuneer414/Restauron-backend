package dev.siraj.restauron.service.scheduledServices.subscriptionSchedulersServices;


import dev.siraj.restauron.entity.enums.AccessLevelStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.subscription.RestaurantSubscription;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import dev.siraj.restauron.repository.subscription.RestaurantSubscriptionRepository;
import dev.siraj.restauron.repository.subscription.SubscriptionPackageRepository;
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


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkSubscriptionStatus() {
        LocalDate today = LocalDate.now();

        // 1) ACTIVE -> GRACE (when endDate < today)
        List<RestaurantSubscription> moveToGrace =
                subscriptionRepo.findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, today);

        for (RestaurantSubscription sub : moveToGrace) {
            sub.setStatus(SubscriptionStatus.GRACE_PERIOD);

            Restaurant restaurant = sub.getRestaurant();
            restaurant.setAccessLevel(AccessLevelStatus.PARTIAL);

            // anchor date for all later calculations
            if (restaurant.getSubscriptionExpiredSince() == null) {
                restaurant.setSubscriptionExpiredSince(sub.getEndDate()); // expiry date (endDate)
            }

            // optional: clear old message if any
            restaurant.setCustomerPageMessage(null);

            emailService.sendSubscriptionExpiredEmail(restaurant.getEmail(), restaurant.getName());
        }
        subscriptionRepo.saveAll(moveToGrace);

        // 2) GRACE -> EXPIRED + READ_ONLY (1 day grace over)
        // Grace should last exactly 1 day after endDate.
        List<RestaurantSubscription> graceOver =
                subscriptionRepo.findByStatusAndEndDateBefore(
                        SubscriptionStatus.GRACE_PERIOD,
                        today.minusDays(1) // endDate < (today - 1) means grace finished
                );

        for (RestaurantSubscription sub : graceOver) {
            sub.setStatus(SubscriptionStatus.EXPIRED);

            Restaurant restaurant = sub.getRestaurant();
            restaurant.setAccessLevel(AccessLevelStatus.READ_ONLY);

            if (restaurant.getSubscriptionExpiredSince() == null) {
                restaurant.setSubscriptionExpiredSince(sub.getEndDate());
            }

            // Customer page still accessible, but should be "read-only" for 10 days
            restaurant.setCustomerPageMessage(
                    "Ordering temporarily disabled. Please contact " +
                            restaurant.getPhone() + " / " + restaurant.getEmail() + " for help."
            );
        }
        subscriptionRepo.saveAll(graceOver); // IMPORTANT: you were missing this

        // 3) EXPIRED(read-only) -> BLOCKED (after 10 days read-only)
        // Block at day 12 after endDate, i.e. endDate < today - 11
        List<RestaurantSubscription> readOnlyWindowOver =
                subscriptionRepo.findByStatusAndEndDateBefore(
                        SubscriptionStatus.EXPIRED,
                        today.minusDays(11)
                );

        for (RestaurantSubscription sub : readOnlyWindowOver) {
            Restaurant restaurant = sub.getRestaurant();
            restaurant.setAccessLevel(AccessLevelStatus.BLOCKED);
            restaurant.setCustomerPageMessage(
                    "Temporarily out of service - Please contact " +
                            restaurant.getPhone() + " / " + restaurant.getEmail() + " for more information."
            );
        }
        // subscriptions are already EXPIRED, so saving subs is enough (restaurants are managed in txn)
        subscriptionRepo.saveAll(readOnlyWindowOver);

        // 4) Warning emails (keep your logic as-is)
        // NOTE: ideally each reminder query should use its own flag (reminder5DaysSent/reminder2DaysSent/...).

        // 5 days
        List<RestaurantSubscription> fiveDaysLeft = subscriptionRepo.findExpiringSubscriptions(today.plusDays(5), false);
        for (RestaurantSubscription sub : fiveDaysLeft) {
            emailService.sendWarningEmail(sub.getRestaurant().getEmail(), sub.getRestaurant().getName(), 5);
            sub.setReminder5DaysSent(true);
        }
        subscriptionRepo.saveAll(fiveDaysLeft);

        // 2 days
        List<RestaurantSubscription> twoDaysLeft = subscriptionRepo.findExpiringSubscriptions(today.plusDays(2), false);
        for (RestaurantSubscription sub : twoDaysLeft) {
            emailService.sendWarningEmail(sub.getRestaurant().getEmail(), sub.getRestaurant().getName(), 2);
            sub.setReminder2DaysSent(true);
        }
        subscriptionRepo.saveAll(twoDaysLeft);

        // 1 day
        List<RestaurantSubscription> oneDayLeft = subscriptionRepo.findExpiringSubscriptions(today.plusDays(1), false);
        for (RestaurantSubscription sub : oneDayLeft) {
            emailService.sendWarningEmail(sub.getRestaurant().getEmail(), sub.getRestaurant().getName(), 1);
            sub.setReminder1DaySent(true);
        }
        subscriptionRepo.saveAll(oneDayLeft);

        // 6) Offer expiry cleanup (you already added this)
        List<SubscriptionPackage> expiredOffers = subscriptionPackageRepository.findPackagesWithExpiredOffers(today);
        for (SubscriptionPackage pack : expiredOffers) {
            log.info("Removing expired offer from package: {}", pack.getName());
            pack.setOffer(null);
        }
        if (!expiredOffers.isEmpty()) {
            subscriptionPackageRepository.saveAll(expiredOffers);
        }
    }
}