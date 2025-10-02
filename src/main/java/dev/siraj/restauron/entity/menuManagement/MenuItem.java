package dev.siraj.restauron.entity.menuManagement;

import dev.siraj.restauron.entity.enums.ItemStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob // Best practice for potentially long strings
    private String description;

    @Column(nullable = false)
    private Double price;

    private String imageUrl; // URL from Firebase Storage

    private boolean isVegetarian;

    private boolean isAvailable = true; // Default to true upon creation

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
}

