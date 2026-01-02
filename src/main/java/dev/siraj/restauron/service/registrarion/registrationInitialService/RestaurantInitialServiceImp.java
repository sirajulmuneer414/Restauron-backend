package dev.siraj.restauron.service.registrarion.registrationInitialService;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.registration.RestaurantRegistrationDto;
import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.enums.VerificationStatus;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.exceptionHandlers.customExceptions.RegistrationUserNotFoundException;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.repository.restaurantInitial.RestaurantInitialRepository;
import dev.siraj.restauron.service.cloudinaryService.ImageUploadService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RestaurantInitialServiceImp implements RestaurantInitialService {

    @Autowired
    RestaurantInitialRepository repository;
    @Autowired
    RestaurantRegistrationMapping mapping;
    @Autowired
    ImageUploadService imageUploadService;


    @Override
    @Transactional
    public RestaurantRegistration registerRestaurantForApproval(RestaurantRegistrationDto restaurantRegistrationDto) {

        String aadhaarPhoto = imageUploadService.imageUploader(restaurantRegistrationDto.getAadhaarPhoto(), "Aadhaar-owner");

        RestaurantRegistration restaurantRegistration = mapping.registrationDtoToTableEntityMapping(restaurantRegistrationDto);

        restaurantRegistration.setOwnerAdhaarPhoto(aadhaarPhoto);

        return repository.save(restaurantRegistration);
    }


    @Override
    public Long findRestaurantRegisterationOwnerIdByEmail(String email) {
        RestaurantRegistration restaurantRegistration = repository.findByOwnerEmail(email);

        if(restaurantRegistration == null){
            throw new RegistrationUserNotFoundException("The email you have entered is not registered");
        }


        return restaurantRegistration.getId();


    }

    @Override
    public void otpVerificationSuccessfulEnumChange(Long restaurantOwnerId) {
        RestaurantRegistration restaurantRegistration = repository.findById(restaurantOwnerId).get();

        restaurantRegistration.setOtpStatus(VerificationStatus.VERIFIED);

        repository.save(restaurantRegistration);

    }

    @Override
    public RestaurantRegistration findByUserEmail(String email) {

        return repository.findByOwnerEmail(email);
    }

    @Override
    public boolean registrationEmailExists(String email) {
        System.out.println("In the email checking service method");
        boolean exists = repository.existsByOwnerEmail(email);
        System.out.println(exists);
        return exists;
    }

    @Override
    public Page<RestaurantInitialResponse> findAllRestaurantInitialsByPageRequestDtoMappedToInitialResponse(PageRequestDto pageDetails) {


        Pageable pageable = PageRequest.of(pageDetails.getPageNo(), pageDetails.getSize());


            Specification<RestaurantRegistration> specification = buildSpecification(pageDetails);

            Page<RestaurantRegistration> pageRestaurant= repository.findAll(specification, pageable);

            log.info("Found {} restaurantInitials on page {}", pageRestaurant.getNumberOfElements(), pageDetails.getSize());

            return mapping.restaurantRegistrationToInitialResponsePagingMapping(pageRestaurant);



    }

    private Specification<RestaurantRegistration> buildSpecification(PageRequestDto pageDetails) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> finalPredicate = new ArrayList<>();

            if(StringUtils.hasText(pageDetails.getFilter())){
                try {
                    PendingStatuses status = PendingStatuses.valueOf(pageDetails.getFilter().toUpperCase());

                    finalPredicate.add(criteriaBuilder.equal(root.get("status"), status));

                }catch (IllegalArgumentException e){
                    log.warn("Invalid status filter provided: {}",pageDetails.getFilter());
                }
            }

            if(StringUtils.hasText(pageDetails.getSearch())){

                String searchPatter = "%"+pageDetails.getSearch()+"%";

                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ownerName")), searchPatter),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ownerEmail")), searchPatter),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("restaurantName")),searchPatter)
                );

                finalPredicate.add(searchPredicate);
            }

            return criteriaBuilder.and(finalPredicate.toArray(new Predicate[0]));
        };
    }

    @Override
    public RestaurantRegistration findRestaurantRegistrationById(Long restaurantId) {
        return repository.findById(restaurantId).orElseThrow();
    }

    @Override
    public void deleteRestaurantRegistrationOnApproval(RestaurantRegistration restaurantRegistration) {
        repository.delete(restaurantRegistration);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return repository.existsByOwnerEmail(email);
    }

    @Override
    public boolean restaurantRegistrationExistsById(Long restaurantRegistrationId) {
        return repository.existsById(restaurantRegistrationId);
    }

    @Override
    public void rejectedByAdmin(RestaurantRegistration restaurantRegistration) {
        restaurantRegistration.setStatus(PendingStatuses.REJECTED);

        repository.save(restaurantRegistration);
    }


}
