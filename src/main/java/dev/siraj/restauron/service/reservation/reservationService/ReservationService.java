package dev.siraj.restauron.service.reservation.reservationService;

import dev.siraj.restauron.DTO.reservations.ReservationDto;
import dev.siraj.restauron.entity.enums.ReservationStatus;
import dev.siraj.restauron.entity.restaurant.management.reservation.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationService {
    Page<Reservation> findAllPagesWithFilterAndSearch(int page, int size,String encryptedRestaurantId, ReservationStatus statusEnum, String search);

    @Transactional
    void addReservation(String encryptedRestaurantId, ReservationDto request, String reservationDoneBy);
}
