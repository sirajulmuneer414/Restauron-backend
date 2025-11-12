package dev.siraj.restauron.DTO.reservations;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDto {
    private String name;
    private String email;
    private String phone;
    private LocalDateTime reservationTime;
    private String status; // PENDING/CONFIRMED/CANCELLED
    private String remark;
}