package dev.siraj.restauron.entity.restaurant.management.reservation;

import dev.siraj.restauron.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantEncryptedId;

    private String customerEncryptedId; // Nullable

    private String customerName;        // Nullable

    private String customerEmail;       // Nullable

    @Column(nullable = false)
    private String customerPhone;       // Not null

    @Column(nullable = false)
    private String reservationDate;    // YYYY-MM-DD

    @Column(nullable = false)
    private String reservationTime;     // "HH:mm"

    @Column(nullable = false)
    private Integer noOfPeople;

    @Enumerated(EnumType.STRING)
    private ReservationStatus currentStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_status_timestamp", joinColumns = @JoinColumn(name = "reservation_id"))
    private List<ReservationStatusTimestamp> timestamps = new ArrayList<>();

    private String remark;

    private String reservationDoneBy;
}
