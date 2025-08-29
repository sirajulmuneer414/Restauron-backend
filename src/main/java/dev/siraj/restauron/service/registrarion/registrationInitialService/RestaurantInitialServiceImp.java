package dev.siraj.restauron.service.registrarion.registrationInitialService;

import dev.siraj.restauron.DTO.admin.RestaurantInitialResponse;
import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.registration.RestaurantRegistrationDto;
import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.enums.VerificationStatus;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.exceptionHandlers.customExceptions.RegistrationUserNotFoundException;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.respository.restaurantInitial.RestaurantInitialRepository;
import dev.siraj.restauron.service.registrarion.adminRegistrationService.RegistrationInitialSpecification;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RestaurantInitialServiceImp implements RestaurantInitialService {

    @Autowired
    RestaurantInitialRepository repository;
    @Autowired
    RestaurantRegistrationMapping mapping;


    @Override
    @Transactional
    public RestaurantRegistration registerRestaurantForApproval(RestaurantRegistrationDto restaurantRegistrationDto) {

        RestaurantRegistration restaurantRegistration = mapping.registrationDtoToTableEntityMapping(restaurantRegistrationDto);


       RestaurantRegistration registeredRestaurant = repository.save(restaurantRegistration);


        return registeredRestaurant;
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

        if(pageDetails.isFiltered()) {

            RegistrationInitialSpecification<PendingStatuses> specification = new RegistrationInitialSpecification<>();

            PendingStatuses pendingStatuses = null;
            for (PendingStatuses stat : PendingStatuses.values()) {
                if(stat.name().equals(pageDetails.getFilter())) pendingStatuses = stat;

            }

            if(pendingStatuses != null) {
                Specification<RestaurantRegistration> restaurantRegistrationSpecification = specification.filterRegistrationInitialAccordingToEnum("status", pendingStatuses);

                return mapping.restaurantRegistrationToInitialResponsePagingMapping(repository.findAll(restaurantRegistrationSpecification, pageable));
            }

        }

        return mapping.restaurantRegistrationToInitialResponsePagingMapping(repository.findAll(pageable));

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
