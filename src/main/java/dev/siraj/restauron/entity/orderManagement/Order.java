package dev.siraj.restauron.entity.orderManagement;

import dev.siraj.restauron.entity.enums.OrderStatus;
import dev.siraj.restauron.entity.enums.OrderType;
import dev.siraj.restauron.entity.enums.PaymentMode;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.management.RestaurantTable;
import dev.siraj.restauron.entity.users.Customer;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@Table(name = "restaurant_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A simple, sequential bill number for display purposes
    @Column(nullable = false, unique = true)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(length = 100)
    private String temporaryCustomerName;

    @Column(length = 10)
    private String temporaryCustomerNumber;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_table_id", nullable = true)
    private RestaurantTable restaurantTable;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Column(nullable = false)
    private Double totalAmount;

    private Double reservationFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType; // DINE_IN or TAKE_AWAY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode; // CASH or ONLINE

    @Column(nullable = true)
    private String customerRemarks;

    // For reservations or scheduled take-away
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate orderDate;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalTime orderTime;

    /**
     * JPA lifecycle callback to set temporary bill number before initial persist.
     * This allows the entity to be saved without violating the NOT NULL constraint.
     */
    @PrePersist
    public void prePersist() {
        if (billNumber == null) {
            billNumber = "TEMP-" + java.util.UUID.randomUUID().toString();
        }
    }

    /**
     * JPA lifecycle callback to set final bill number after persist.
     * The ID is now available, so we can create the proper bill number format.
     * Note: This requires a flush and refresh or a separate update query to persist
     * the change.
     */
    @PostPersist
    public void postPersist() {
        // Note: This updates the in-memory object but doesn't automatically persist to
        // DB
        // The service layer should call repository.save() after this to persist the
        // final bill number
        this.billNumber = "ORD-" + this.id;
    }
}
