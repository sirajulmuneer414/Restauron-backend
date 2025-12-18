package dev.siraj.restauron.entity.subscription;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Embeddable Offer class for SubscriptionPackage

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    private Double discount;         // Value (e.g. 20.0 percent, or 500.0 cash)
    private String discountType;     // "percent" or "cash"
    private String discountDescription;
    private LocalDate expiry;
}
