package dev.siraj.restauron.entity.subscription;

import dev.siraj.restauron.entity.enums.subscription.PackageStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


    // SubscriptionPackage entity representing subscription packages for restaurants

@Entity
@Table(name = "subscription_package")
@Data
public class SubscriptionPackage {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer durationAmount; // e.g. 30

    @Column(nullable = false)
    private String durationType;    // e.g. "days", "weeks", "months", "years"

    @Column(nullable = false)
    private Double price;

    private String description;

    @Enumerated(EnumType.STRING)
    private PackageStatus status; // ACTIVE, HIDDEN, ARCHIVED

    @Embedded
    private Offer offer; // nullable, for offer block

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "subscriptionPackage", fetch = FetchType.LAZY)
    private List<RestaurantSubscription> subscriptions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
