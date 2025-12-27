package dev.siraj.restauron.restController.owner;


import dev.siraj.restauron.DTO.owner.subscription.OwnerSubscriptionHomeDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.owner.ownerSubscriptionService.OwnerSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RolesAllowed(roles = {"OWNER"})
@RequestMapping("/owner/subscription")
public class OwnerSubscriptionHomeController {


    @Autowired
    private OwnerSubscriptionService subscriptionHomeService;


    @GetMapping("/home")
    public ResponseEntity<OwnerSubscriptionHomeDTO> getSubscriptionHome(
            @RequestHeader("X-Restaurant-Id") String encryptedRestaurantId
    ) {
        OwnerSubscriptionHomeDTO dto = subscriptionHomeService.getSubscriptionHome(encryptedRestaurantId);
        return ResponseEntity.ok(dto);
    }
}
