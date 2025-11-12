package dev.siraj.restauron.entity.menuManagement;

import dev.siraj.restauron.entity.enums.AvailabilityStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer amountOfMenuItem = 0;

    @Lob
    private String description;

    @Enumerated(value = EnumType.STRING)
    private AvailabilityStatus status;  // The enums are AVAILABLE, UNAVAILABLE, LOW, RESTOCKING

    @ManyToOne
    private Restaurant restaurant;


    public void addedMenuItem(){
        amountOfMenuItem += 1;
    }

    public void removedMenuItem(){
        amountOfMenuItem -= 1;
    }


}
