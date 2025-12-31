package dev.siraj.restauron.restController.subscription.admin;


import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPackageResponseDTO;
import dev.siraj.restauron.entity.enums.subscription.PackageStatus;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import dev.siraj.restauron.service.subscription.interfaces.SubscriptionPackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// REST controller for managing subscription packages in the admin panel

@RestController
@RequestMapping("/api/admin/subscriptions")
@Slf4j
public class SubscriptionPackageController {


    private final SubscriptionPackageService subscriptionPackageService;

    @Autowired
    public SubscriptionPackageController(SubscriptionPackageService subscriptionPackageService) {
        this.subscriptionPackageService = subscriptionPackageService;
    }

    // -------------------------------------------- CONTROLLER METHODS -------------------------------------------------------

    /**
     * Endpoint to list all subscription packages
     * @return list of SubscriptionPackageResponseDTO
     */
    @GetMapping("/packages")
    public ResponseEntity<List<SubscriptionPackageResponseDTO>> listPackages() {
        log.info("Fetching all subscription packages");
        return ResponseEntity.ok(subscriptionPackageService.getAllPackages());
    }


    /**
     * Endpoint to add or update a subscription package
     *
     * @return SubscriptionPackageResponseDTO this is particularly useful when updating a package and want to see the updated details
     */
    @PostMapping("/package")
    public ResponseEntity<SubscriptionPackageResponseDTO> addPackage(@RequestBody SubscriptionPackage pkg) {
        log.info("Creating new subscription package: {}", pkg.getName());
        return new ResponseEntity<>(subscriptionPackageService.createOrUpdatePackage(pkg, null), HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an existing subscription package
     *
     * @return SubscriptionPackageResponseDTO this is particularly useful when updating a package and want to see the updated details
     */
    @PutMapping("/package/{id}")
    public ResponseEntity<SubscriptionPackageResponseDTO> updatePackage(@PathVariable Long id, @RequestBody SubscriptionPackage pkg) {
        log.info("Updating subscription package with ID: {}", id);
        return new ResponseEntity<>(subscriptionPackageService.createOrUpdatePackage(pkg, id), HttpStatus.OK);
    }

    /**
     * Endpoint to set the status of a subscription package
     * @param id subscription package ID
     * @param statusMap map containing the new status
     */
    @PutMapping("/package/{id}/status")
    public void setStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        log.info("Setting status of subscription package with ID: {} to {}", id, statusMap.get("status"));
        subscriptionPackageService.toggleStatus(id, PackageStatus.valueOf(statusMap.get("status")));
    }

    /**
     * Endpoint to archive a subscription package
     * @param id subscription package ID
     */
    @DeleteMapping("/package/{id}")
    public void archivePackage(@PathVariable Long id) {
        log.info("Archiving subscription package with ID: {}", id);
        subscriptionPackageService.archivePackage(id);
    }

    /**
     * Endpoint to get restaurants subscribed to a specific package
     * @param packageId subscription package ID
     * @return list of maps containing restaurant details
     */
    @GetMapping("/usage/{packageId}")
    public ResponseEntity<List<RestaurantSummaryDTO>> getSubscribedRestaurants(@PathVariable Long packageId) {
        log.info("Fetching restaurants subscribed to package ID: {}", packageId);
        return ResponseEntity.ok(subscriptionPackageService.getSubscribedRestaurants(packageId));
    }
}

