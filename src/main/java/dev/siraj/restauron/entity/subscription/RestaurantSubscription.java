package dev.siraj.restauron.entity.subscription;



import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

    // RestaurantSubscription entity representing a restaurant's subscription to a package

@Entity
@Table(name = "restaurant_subscription")
@Data
public class RestaurantSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The Restaurant who bought the sub
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    // The Package they bought (Snapshot or Reference)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private SubscriptionPackage subscriptionPackage;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // e.g., ACTIVE, EXPIRED, CANCELLED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    // Flags to ensure we don't send the same email twice
    private boolean reminder5DaysSent = false;
    private boolean reminder2DaysSent = false;
    private boolean reminder1DaySent = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}