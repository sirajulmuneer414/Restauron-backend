package dev.siraj.restauron.DTO.owner.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerSubscriptionHomeDTO {

    private CurrentSubscriptionDTO currentSubscription;
    private List<SubscriptionPaymentDTO> recentPayments;
}
