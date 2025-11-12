package dev.siraj.restauron.entity.restaurant.management.reservation;

import dev.siraj.restauron.entity.enums.ReservationStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Customer;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Restaurant restaurant;

    private String name;
    private String email;
    private String phone;

    @Column(name = "reservation_date")
    private LocalDate reservationTime;

    @Column(name = "reservation_time")
    private String time;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // e.g. "PENDING", "CONFIRMED", "CANCELLED"

    private String remark;

    private Integer numberOfQuests;
}
