package dev.siraj.restauron.service.subscription.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentHistoryDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentInitiateDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPaymentVerifyDTO;
import dev.siraj.restauron.config.payment.razorpay.RazorpayConfig;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.subscription.RestaurantSubscription;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import dev.siraj.restauron.entity.subscription.SubscriptionPayment;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.respository.subscription.RestaurantSubscriptionRepository;
import dev.siraj.restauron.respository.subscription.SubscriptionPackageRepository;
import dev.siraj.restauron.respository.subscription.payment.SubscriptionPaymentRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.subscription.payment.interfaces.SubscriptionPaymentService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Implementation of subscription payment services

@Service
@Slf4j
public class SubscriptionPaymentServiceImp implements SubscriptionPaymentService {

    @Autowired
    private SubscriptionPackageRepository packageRepository;

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private RazorpayConfig razorpayConfig;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SubscriptionPaymentRepository paymentRepository;

    @Autowired
    private RestaurantSubscriptionRepository subscriptionRepository;

    @Autowired
    private IdEncryptionService idEncryptionService;

    /**
     * Create a subscription order in Razorpay for the given package and restaurant.
     *
     * @param packageId    ID of the subscription package
     * @param restaurantId ID of the restaurant
     * @return SubscriptionPaymentInitiateDTO containing order details
     * @throws RazorpayException if there is an error creating the order
     */
    @Override
    public SubscriptionPaymentInitiateDTO createSubscriptionOrder(Long packageId, Long restaurantId) throws RazorpayException {

        log.info("Creating subscription order for packageId: {} and restaurantId: {}", packageId, restaurantId);

        SubscriptionPackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        // Logic to calculate final price (apply Offer if exists)
        double finalPrice = pkg.getPrice();
        if(pkg.getOffer() != null && pkg.getOffer().getDiscount() > 0) {
            // Simplified offer logic
            if("PERCENT".equalsIgnoreCase(pkg.getOffer().getDiscountType())) {
                finalPrice = finalPrice - (finalPrice * (pkg.getOffer().getDiscount() / 100));
            } else {
                finalPrice = finalPrice - pkg.getOffer().getDiscount();
            }
        }

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(finalPrice * 100)); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        orderRequest.put("notes", new JSONObject().put("restaurant_id", restaurantId).put("package_id", packageId));

        Order order = razorpayClient.orders.create(orderRequest);

        System.out.println(order.get("id").toString());



        return new SubscriptionPaymentInitiateDTO(
                order.get("id").toString(),
                order.get("currency").toString(),
                (int) order.get("amount")
        );
    }

    /**
     * Verify the payment signature and activate the subscription for the restaurant.
     *
     * @param data         SubscriptionPaymentVerifyDTO containing payment details
     * @param restaurantId ID of the restaurant
     * @throws Exception if verification fails or any other error occurs
     */
    @Transactional
    @Override
    public void verifyAndActivateSubscription(SubscriptionPaymentVerifyDTO data, Long restaurantId) throws Exception {
        // 1. Verify Signature
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", data.getRazorpayOrderId());
        options.put("razorpay_payment_id", data.getRazorpayPaymentId());
        options.put("razorpay_signature", data.getRazorpaySignature());

        boolean isValid = Utils.verifyPaymentSignature(options, razorpayConfig.getKeySecret());
        // NOTE: Inject keySecret from config into this service to use here

        if (!isValid) {
            throw new RuntimeException("Payment Signature Verification Failed");
        }

        // 2. Fetch Entities
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        SubscriptionPackage pkg = packageRepository.findById(data.getPackageId()).orElseThrow();

        // 3. Save Payment Record
        SubscriptionPayment payment = new SubscriptionPayment();
        payment.setRazorpayPaymentId(data.getRazorpayPaymentId());
        payment.setRazorpayOrderId(data.getRazorpayOrderId());
        payment.setAmount(Double.parseDouble(data.getAmountPaid())); // Store base price or calculated price
        payment.setRestaurant(restaurant);
        payment.setSubscriptionPackage(pkg);
        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);

        // 4. Create/Update Subscription
        // Disable old active subscription if any
        subscriptionRepository.findByRestaurant_IdAndStatus(restaurantId, SubscriptionStatus.ACTIVE)
                .ifPresent(sub -> {
                    sub.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(sub);
                });

        RestaurantSubscription newSub = new RestaurantSubscription();
        newSub.setRestaurant(restaurant);
        newSub.setSubscriptionPackage(pkg);
        newSub.setStatus(SubscriptionStatus.ACTIVE);
        newSub.setStartDate(LocalDate.now());

        // Calculate End Date based on Duration
        LocalDate endDate = LocalDate.now();
        if ("days".equalsIgnoreCase(pkg.getDurationType())) endDate = endDate.plusDays(pkg.getDurationAmount());
        else if ("months".equalsIgnoreCase(pkg.getDurationType())) endDate = endDate.plusMonths(pkg.getDurationAmount());
        else if ("years".equalsIgnoreCase(pkg.getDurationType())) endDate = endDate.plusYears(pkg.getDurationAmount());

        newSub.setEndDate(endDate);

        subscriptionRepository.save(newSub);
    }

    /**
     * Retrieve all subscription payment history records.
     *
     * @return List of SubscriptionPaymentHistoryDTO containing payment history details
     */
    @Override
    public List<SubscriptionPaymentHistoryDTO> getAllPaymentHistory() {
        return paymentRepository.findAllByOrderByPaymentDateDesc().stream().map(payment -> {
            SubscriptionPaymentHistoryDTO dto = new SubscriptionPaymentHistoryDTO();
            dto.setId(idEncryptionService.encryptLongId(payment.getId()));
            dto.setRazorpayPaymentId(payment.getRazorpayPaymentId());
            dto.setRestaurantName(payment.getRestaurant().getName()); // Ensure Restaurant entity has getName()
            dto.setPackageName(payment.getSubscriptionPackage().getName());
            dto.setAmount(payment.getAmount());
            dto.setPaymentDate(payment.getPaymentDate().toString());
            return dto;
        }).toList();
    }
    }
