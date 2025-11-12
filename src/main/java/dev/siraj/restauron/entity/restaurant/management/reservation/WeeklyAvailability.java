package dev.siraj.restauron.entity.restaurant.management.reservation;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
@Table(name = "weekly_availability")
public class WeeklyAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantId;

    @Column(nullable = false)
    private String dayOfTheWeek;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "weekly_availability_slots", joinColumns = @JoinColumn(name = "weekly_availability_id"))
    private List<Slots> slots;

    private String createdBy = "OWNER";

}
