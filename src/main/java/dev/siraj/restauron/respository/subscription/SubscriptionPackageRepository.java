package dev.siraj.restauron.respository.subscription;


import dev.siraj.restauron.entity.enums.subscription.PackageStatus;
import dev.siraj.restauron.entity.enums.subscription.SubscriptionStatus;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Repository interface for SubscriptionPackage entity

@Repository
public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, Long> {
  List<SubscriptionPackage> findByStatus(PackageStatus packageStatus);

  @Query("SELECT p FROM SubscriptionPackage p WHERE p.offer IS NOT NULL AND p.offer.expiry < :today")
  List<SubscriptionPackage> findPackagesWithExpiredOffers(LocalDate today);

}
