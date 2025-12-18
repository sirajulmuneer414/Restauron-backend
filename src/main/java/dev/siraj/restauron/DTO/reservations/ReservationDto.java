package dev.siraj.restauron.DTO.reservations;

import dev.siraj.restauron.entity.restaurant.management.reservation.ReservationStatusTimestamp;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationDto {

    private String customerEncryptedId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String reservationDate; // YYYY-MM-DD
    private String reservationTime; // "HH:mm"
    private Integer noOfPeople;
    private String currentStatus;

    private List<ReservationStatusTimestamp> timestamps;

    private String remark;

    private String reservationDoneBy;
}