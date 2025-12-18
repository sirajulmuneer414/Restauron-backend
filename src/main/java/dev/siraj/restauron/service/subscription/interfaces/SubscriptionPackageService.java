package dev.siraj.restauron.service.subscription.interfaces;

import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPackageResponseDTO;
import dev.siraj.restauron.entity.enums.subscription.PackageStatus;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface SubscriptionPackageService {
    List<SubscriptionPackageResponseDTO> getAllPackages();

    SubscriptionPackageResponseDTO getPackage(Long id);

    @Transactional
    SubscriptionPackageResponseDTO createOrUpdatePackage(SubscriptionPackage pkg, Long id);

    @Transactional
    void toggleStatus(Long id, PackageStatus status);

    @Transactional
    void archivePackage(Long id);

    List<RestaurantSummaryDTO> getSubscribedRestaurants(Long packageId);

    List<SubscriptionPackageResponseDTO> getAllActivePackages();
}
