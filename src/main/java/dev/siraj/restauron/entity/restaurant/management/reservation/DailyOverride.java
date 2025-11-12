package dev.siraj.restauron.entity.restaurant.management.reservation;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "daily_override")
@Data
public class DailyOverride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantId; // or Long if you prefer

    @Column(nullable = false)
    private String date; // e.g., "2025-12-25" (YYYY-MM-DD)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "daily_override_slots", joinColumns = @JoinColumn(name = "daily_override_id"))
    private List<Slots> slots;

    private String createdBy = "OWNER";
}
