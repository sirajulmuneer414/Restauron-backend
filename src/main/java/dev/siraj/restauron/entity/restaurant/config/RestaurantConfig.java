package dev.siraj.restauron.entity.restaurant.config;


import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


    // Entity representing restaurant configuration settings

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Visual Identity ---
    @Column(length = 500) // Allow longer URL for Cloudinary
    private String bannerUrl;

    private String primaryColor;   // e.g. #f59e0b
    private String secondaryColor; // e.g. #000000
    private String buttonTextColor;// e.g. #000000

    // --- Quotes & Text ---
    private String centerQuote;    // Hero section
    private String topLeftQuote;   // Header
    private String bestFeature;    // "Best Seafood"

    // --- Operations ---
    private String locationText;   // Address display
    private String openingTime;    // "09:00"
    private String closingTime;    // "22:00"

    private boolean isManualOpen;  // The manual toggle value (Open vs Closed)
    private boolean useManualOpen; // The mode (True = Manual, False = Auto)

    // --- Relationship ---
    @OneToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}

