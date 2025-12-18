package dev.siraj.restauron.DTO.subscription;


import lombok.AllArgsConstructor;
import lombok.Data;

    // DTO for initiating a subscription payment

@Data
@AllArgsConstructor
public class SubscriptionPaymentInitiateDTO {
    private String id;

    private String currency;

    private Integer amount;
}
