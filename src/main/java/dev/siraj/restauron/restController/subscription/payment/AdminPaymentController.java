package dev.siraj.restauron.restController.subscription.payment;




import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentHistoryDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.subscription.payment.interfaces.SubscriptionPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST controller for admin payment operations

@RestController
@RolesAllowed(roles = {"ADMIN"})
@RequestMapping("/api/admin/payments")
@Slf4j
public class AdminPaymentController
{


    private final SubscriptionPaymentService paymentService;

    @Autowired
    public AdminPaymentController(SubscriptionPaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @GetMapping("/history")
    public ResponseEntity<List<SubscriptionPaymentHistoryDTO>> getHistory() {
        log.info("Received request to fetch all payment history");
        return ResponseEntity.ok(paymentService.getAllPaymentHistory());
    }

}
