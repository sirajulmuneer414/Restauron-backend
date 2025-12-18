package dev.siraj.restauron.DTO.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

    // DTO for verifying a subscription payment

@Data
public class SubscriptionPaymentVerifyDTO {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String amountPaid;
    private Long packageId;
}
