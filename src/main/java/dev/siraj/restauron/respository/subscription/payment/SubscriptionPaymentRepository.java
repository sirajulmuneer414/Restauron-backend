package dev.siraj.restauron.respository.subscription.payment;

import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import dev.siraj.restauron.entity.subscription.SubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

// Repository for subscription payment data access

@Repository
public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

    List<SubscriptionPayment> findAllByOrderByPaymentDateDesc();

    List<SubscriptionPayment> findTop5ByRestaurant_IdOrderByPaymentDateDesc(Long restaurantId);
}
