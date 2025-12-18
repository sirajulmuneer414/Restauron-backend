package dev.siraj.restauron.service.subscription;

import dev.siraj.restauron.DTO.admin.stats.RestaurantSummaryDTO;
import dev.siraj.restauron.DTO.subscription.SubscriptionPackageResponseDTO;
import dev.siraj.restauron.entity.enums.subscription.PackageStatus;
import dev.siraj.restauron.entity.subscription.SubscriptionPackage;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.respository.subscription.SubscriptionPackageRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.subscription.interfaces.SubscriptionPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

    // Service implementation for SubscriptionPackageService

@Service
public class SubscriptionPackageServiceImp implements SubscriptionPackageService {


        @Autowired
        private SubscriptionPackageRepository packageRepo;

        @Autowired
        private RestaurantRepository restaurantRepo;

        @Autowired
        private IdEncryptionService idEncryptionService;

// -------------------------------------------------- SERVICE METHODS ---------------------------------------------------------
        /**
         * Method to get all subscription packages
         * @return list of SubscriptionPackageResponseDTO
         */
        @Override
        public List<SubscriptionPackageResponseDTO> getAllPackages() {
            return packageRepo.findAll().stream()
                    .map(pkg -> toDto(pkg, false))
                    .collect(Collectors.toList());
        }


        /**
         * Method to get a subscription package by ID
         * @param id subscription package ID
         * @return SubscriptionPackageResponseDTO
         */
        @Override
        public SubscriptionPackageResponseDTO getPackage(Long id) {
            return packageRepo.findById(id).map(pkg -> toDto(pkg, false)).orElseThrow();
        }

        /**
         * Method to create or update a subscription package
         * @param pkg SubscriptionPackage entity
         * @param id subscription package ID (null for create)
         * @return SubscriptionPackageResponseDTO
         */
        @Transactional
        @Override
        public SubscriptionPackageResponseDTO createOrUpdatePackage(SubscriptionPackage pkg, Long id) {
            if (id != null) {
                SubscriptionPackage existing = packageRepo.findById(id).orElseThrow();
                // update fields (preferably with a copy/update utility)
                existing.setName(pkg.getName());
                existing.setDurationAmount(pkg.getDurationAmount());
                existing.setDurationType(pkg.getDurationType());
                existing.setPrice(pkg.getPrice());
                existing.setDescription(pkg.getDescription());
                existing.setOffer(pkg.getOffer());
                existing.setStatus(pkg.getStatus());
                return toDto(packageRepo.save(existing), false);
            } else {
                pkg.setId(null);
                return toDto(packageRepo.save(pkg), false);
            }
        }

        /**
         * Method to toggle the status of a subscription package
         * @param id subscription package ID
         * @param status new PackageStatus
         */
        @Transactional
        @Override
        public void toggleStatus(Long id, PackageStatus status) {
            SubscriptionPackage pkg = packageRepo.findById(id).orElseThrow();
            pkg.setStatus(status);
            packageRepo.save(pkg);
        }

        /**
         * Method to archive a subscription package
         * @param id subscription package ID
         */
        @Transactional
        @Override
        public void archivePackage(Long id) {
            SubscriptionPackage pkg = packageRepo.findById(id).orElseThrow();
            pkg.setStatus(PackageStatus.ARCHIVED);
            packageRepo.save(pkg);
        }

        /**
         * Method to get subscribed restaurants for a subscription package
         *
         * @param packageId subscription package ID
         * @return list of maps with restaurant id and name
         */
        @Override
        public List<RestaurantSummaryDTO> getSubscribedRestaurants(Long packageId) {
            SubscriptionPackage pkg = packageRepo.findById(packageId).orElseThrow();
            return pkg.getSubscriptions().stream()
                    .map(r -> {
                        RestaurantSummaryDTO rDto = new RestaurantSummaryDTO();
                        rDto.setName(r.getRestaurant().getName());
                        rDto.setCreatedAt(r.getCreatedAt().toString());
                        rDto.setStatus(r.getStatus().toString());
                        rDto.setRestaurantEncryptedId(idEncryptionService.encryptLongId(r.getId()));
                        return rDto;
                    })
                    .collect(Collectors.toList());

        }

        /**
         * Method to get all active subscription packages
         * @return list of SubscriptionPackageResponseDTO
         */
        @Override
        public List<SubscriptionPackageResponseDTO> getAllActivePackages() {
            return packageRepo.findByStatus(PackageStatus.ACTIVE).stream()
                    .map(pkg -> toDto(pkg, false))
                    .collect(Collectors.toList());
        }

// ------------------------------------------------ HELPER METHODS ----------------------------------------------------------

        /**
         * Helper method to convert SubscriptionPackage entity to SubscriptionPackageResponseDTO
         * @param pkg SubscriptionPackage entity
         * @param includeSubscribers whether to include subscribed restaurants details
         * @return SubscriptionPackageResponseDTO
         */
        private SubscriptionPackageResponseDTO toDto(SubscriptionPackage pkg, boolean includeSubscribers) {


                SubscriptionPackageResponseDTO dto = new SubscriptionPackageResponseDTO();

                dto.setId(pkg.getId());
                dto.setName(pkg.getName());
                dto.setDurationAmount(pkg.getDurationAmount());
                dto.setDurationType(pkg.getDurationType());
                dto.setPrice(pkg.getPrice());
                dto.setOffer(pkg.getOffer());
                dto.setStatus(pkg.getStatus());
                dto.setDescription(pkg.getDescription());
                dto.setCreatedAt(pkg.getCreatedAt() != null ? pkg.getCreatedAt().toLocalDate().toString() : "");
                dto.setUpdatedAt(pkg.getUpdatedAt() != null ? pkg.getUpdatedAt().toLocalDate().toString() : "");
                dto.setSubscribedRestaurantsCount(pkg.getSubscriptions() != null ? pkg.getSubscriptions().size() : 0);
                if (includeSubscribers) {
                    List<RestaurantSummaryDTO> subscribers = pkg.getSubscriptions().stream()
                            .map(r -> {
                                RestaurantSummaryDTO rDto = new RestaurantSummaryDTO();
                                rDto.setName(r.getRestaurant().getName());
                                rDto.setCreatedAt(r.getCreatedAt().toString());
                                rDto.setStatus(r.getStatus().toString());
                                rDto.setRestaurantEncryptedId(idEncryptionService.encryptLongId(r.getId()));
                                return rDto;
                            })
                            .collect(Collectors.toList());
                    dto.setSubscribedRestaurants(subscribers);
                }


            return dto;
        }
}
