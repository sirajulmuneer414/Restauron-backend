package dev.siraj.restauron.entity.subscription;


import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

    // SubscriptionPayment entity representing payments for subscription packages

@Entity
@Data
public class SubscriptionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String razorpayPaymentId; // The 'pay_...' ID

    @Column(nullable = false)
    private String razorpayOrderId;   // The 'order_...' ID

    @Column(nullable = false)
    private Double amount;            // Stored in Rupees (e.g. 999.00)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private SubscriptionPackage subscriptionPackage;

    @CreationTimestamp
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private String status; // SUCCESS, FAILED
}
