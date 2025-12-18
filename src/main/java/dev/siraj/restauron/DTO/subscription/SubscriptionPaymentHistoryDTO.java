package dev.siraj.restauron.DTO.subscription;

import lombok.Data;

    // DTO for subscription payment history

@Data
public class SubscriptionPaymentHistoryDTO {
    private String id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String restaurantName;
    private Long packageId;
    private String packageName;
    private Double amount;
    private String paymentDate;
}
