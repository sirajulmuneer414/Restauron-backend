package dev.siraj.restauron.service.subscription.payment.interfaces;


import com.razorpay.RazorpayException;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentHistoryDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentInitiateDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentVerifyDTO;
import jakarta.transaction.Transactional;

import java.util.List;

// Interface for subscription payment services

public interface SubscriptionPaymentService {
    SubscriptionPaymentInitiateDTO createSubscriptionOrder(Long packageId, Long restaurantId) throws RazorpayException;

    @Transactional
    void verifyAndActivateSubscription(SubscriptionPaymentVerifyDTO data, Long restaurantId) throws Exception;

    List<SubscriptionPaymentHistoryDTO> getAllPaymentHistory();
}
