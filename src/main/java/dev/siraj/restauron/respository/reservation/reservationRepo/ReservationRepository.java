package dev.siraj.restauron.respository.reservation.reservationRepo;

import dev.siraj.restauron.entity.enums.ReservationStatus;
import dev.siraj.restauron.entity.restaurant.management.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> , JpaSpecificationExecutor<Reservation> {
    long countByReservationDateAndCurrentStatus(String today, ReservationStatus reservationStatus);


    @Query("SELECT r.reservationDate, COUNT(r) FROM Reservation r WHERE r.reservationDate BETWEEN :start AND :end GROUP BY r.reservationDate")
    List<Object[]> countReservationsByDateBetween(@Param("start") String start, @Param("end") String end);

    @Query("SELECT r.currentStatus, COUNT(r) FROM Reservation r GROUP BY r.currentStatus")
    List<Object[]> countReservationsByStatus();
}
