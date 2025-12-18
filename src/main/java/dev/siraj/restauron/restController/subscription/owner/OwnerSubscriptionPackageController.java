package dev.siraj.restauron.restController.subscription.owner;

import dev.siraj.restauron.DTO.subscription.SubscriptionPackageResponseDTO;
import dev.siraj.restauron.customAnnotations.authorization.RolesAllowed;
import dev.siraj.restauron.service.subscription.interfaces.SubscriptionPackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


// REST controller for Owner Subscription Package related endpoints

@RestController
@RolesAllowed(roles = {"OWNER"})
@RequestMapping("/owner/subscription")
@Slf4j
public class OwnerSubscriptionPackageController {

    @Autowired
    private SubscriptionPackageService subscriptionPackageService;

    /**
     * Endpoint to list all active subscription packages for owners
     * @return list of SubscriptionPackageResponseDTO
     */
    @GetMapping("/packages")
    public ResponseEntity<List<SubscriptionPackageResponseDTO>> listPackages() {
        log.info("Fetching all active subscription packages for owner");
        return ResponseEntity.ok(subscriptionPackageService.getAllActivePackages());
    }

}
