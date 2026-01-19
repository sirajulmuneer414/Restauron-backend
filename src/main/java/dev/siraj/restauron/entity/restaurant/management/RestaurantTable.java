package dev.siraj.restauron.entity.restaurant.management;

import dev.siraj.restauron.entity.enums.table.TableStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "restaurant_table") // Use a non-reserved name
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity = 4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TableStatus status = TableStatus.AVAILABLE;

    // The QR code itself will be generated on the frontend.

}