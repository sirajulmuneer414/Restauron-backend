package dev.siraj.restauron.restController.subscription.payment;

import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentInitiateDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentVerifyDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.repository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.subscription.payment.interfaces.SubscriptionPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

// REST controller for handling owner payment-related endpoints
@RestController
@RequestMapping("/api/owner/payments")
@RolesAllowed(roles = {"OWNER", "ADMIN"})
@Slf4j
public class OwnerPaymentController {


    private final IdEncryptionService idEncryptionService;

    private final SubscriptionPaymentService paymentService;

    private final RestaurantRepository restaurantRepository;


    public OwnerPaymentController(SubscriptionPaymentService paymentService, IdEncryptionService idEncryptionService, RestaurantRepository restaurantRepository) {
        this.idEncryptionService = idEncryptionService;
        this.restaurantRepository = restaurantRepository;
        this.paymentService = paymentService;
    }

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    /**
     * Create a subscription order for a given package by the restaurant.
     *
     * @param packageId              ID of the subscription package
     * @param encryptedRestaurantId  Encrypted ID of the restaurant from request header
     * @return ResponseEntity containing SubscriptionPaymentInitiateDTO
     * @throws Exception if there is an error during order creation
     */
    @PostMapping("/create-order/{packageId}")
    public ResponseEntity<SubscriptionPaymentInitiateDTO> createOrder(
            @PathVariable Long packageId,
            @RequestHeader("X-Restaurant-Id")String encryptedRestaurantId) throws Exception {
        log.info("Received request to create order for packageId: {} by restaurantId: {}", packageId, encryptedRestaurantId);

        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        return ResponseEntity.ok(paymentService.createSubscriptionOrder(packageId, restaurantId));
    }

    /**
     * Verify the payment and activate the subscription for the restaurant.
     *
     * @param verifyDto              DTO containing payment verification details
     * @param encryptedRestaurantId  Encrypted ID of the restaurant from request header
     * @return ResponseEntity with success message
     * @throws Exception if there is an error during verification or activation
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody SubscriptionPaymentVerifyDTO verifyDto,
            @RequestHeader("X-Restaurant-Id")String encryptedRestaurantId) throws Exception {
        log.info("Received payment verification request for restaurantId: {}", encryptedRestaurantId);
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        paymentService.verifyAndActivateSubscription(verifyDto, restaurantId);
        return ResponseEntity.ok("Payment Verified and Subscription Activated");
    }


    @GetMapping("/razorpay-key")
    public ResponseEntity<String> getRazorpayKey(@RequestHeader("X-Restaurant-Id")String encryptedRestaurantId) throws BadCredentialsException{
        log.info("Received request to fetch razorpay key ID from {}", encryptedRestaurantId);
        Long restaurantId = idEncryptionService.decryptToLongId(encryptedRestaurantId);
        boolean existById = restaurantRepository.existsById(restaurantId);

        if(!existById) throw new BadCredentialsException("Restaurant is not found for this ID");

        return ResponseEntity.ok(razorpayKeyId);
    }

}
