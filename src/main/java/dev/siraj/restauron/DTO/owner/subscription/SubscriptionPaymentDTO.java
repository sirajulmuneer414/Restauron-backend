package dev.siraj.restauron.DTO.owner.subscription;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPaymentDTO {

    private Long id;
    private LocalDate paymentDate;
    private Double amount;
    private String method;
    private String status;
    private String reference;
}
