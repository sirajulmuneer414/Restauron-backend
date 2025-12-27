package dev.siraj.restauron.service.owner.ownerSubscriptionService;

import dev.siraj.restauron.DTO.owner.subscription.CurrentSubscriptionDTO;
import dev.siraj.restauron.DTO.owner.subscription.OwnerSubscriptionHomeDTO;
import dev.siraj.restauron.DTO.owner.subscription.SubscriptionPaymentDTO;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.subscription.RestaurantSubscription;
import dev.siraj.restauron.entity.subscription.SubscriptionPayment;
import dev.siraj.restauron.respository.subscription.RestaurantSubscriptionRepository;
import dev.siraj.restauron.respository.subscription.payment.SubscriptionPaymentRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerSubscriptionServiceImp implements OwnerSubscriptionService{

    @Autowired
    private IdEncryptionService idEncryptionService;

    @Autowired
    private RestaurantSubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionPaymentRepository paymentRepository;

    @Override
    public OwnerSubscriptionHomeDTO getSubscriptionHome(String restaurantEncryptedId) {

        // 1. Resolve restaurantId from owner user id
        Long restaurantId = idEncryptionService.decryptToLongId(restaurantEncryptedId);

        // 2. Current subscription (ACTIVE)
        CurrentSubscriptionDTO currentSubscriptionDTO = subscriptionRepository
                .findFirstByRestaurantIdAndStatusOrderByEndDateDesc(restaurantId, SubscriptionStatus.ACTIVE)
                .map(this::mapToCurrentSubscriptionDTO)
                .orElseGet(() -> null); // no active subscription

        // 3. Recent payments
        List<SubscriptionPayment> payments =
                paymentRepository.findTop5ByRestaurant_IdOrderByPaymentDateDesc(restaurantId);

        List<SubscriptionPaymentDTO> paymentDTOs = payments.stream()
                .map(this::mapToPaymentDTO)
                .collect(Collectors.toList());

        return new OwnerSubscriptionHomeDTO(currentSubscriptionDTO, paymentDTOs);

    }

    // ------------------------------------------------------------------ HELPER METHODS ------------------------------------------------------------------ //


    private CurrentSubscriptionDTO mapToCurrentSubscriptionDTO(RestaurantSubscription subscription) {
        CurrentSubscriptionDTO dto = new CurrentSubscriptionDTO();
        dto.setPlanName(subscription.getSubscriptionPackage().getName());
        dto.setStartDate(subscription.getStartDate().toString());
        dto.setEndDate(subscription.getEndDate().toString());
        dto.setStatus(subscription.getStatus().name());
        dto.setDaysLeft(LocalDate.now().until(subscription.getEndDate()).getDays());
        return dto;
    }

    private SubscriptionPaymentDTO mapToPaymentDTO(SubscriptionPayment payment) {
        SubscriptionPaymentDTO dto = new SubscriptionPaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate().toLocalDate());
        dto.setMethod("RAZORPAY"); // Hardcoded for now
        dto.setStatus(payment.getStatus());
        dto.setReference(payment.getRazorpayPaymentId());
        return dto;
    }
}
