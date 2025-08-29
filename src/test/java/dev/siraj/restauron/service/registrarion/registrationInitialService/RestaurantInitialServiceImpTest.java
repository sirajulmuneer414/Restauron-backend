
package dev.siraj.restauron.service.registrarion.registrationInitialService;

import dev.siraj.restauron.DTO.registration.RestaurantRegistrationDto;
import dev.siraj.restauron.entity.enums.VerificationStatus;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.exceptionHandlers.customExceptions.RegistrationUserNotFoundException;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.respository.restaurantInitial.RestaurantInitialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantInitialServiceImpTest {

    @Mock
    private RestaurantInitialRepository repository;

    @Mock
    private RestaurantRegistrationMapping mapping;

    @InjectMocks
    private RestaurantInitialServiceImp service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterRestaurantForApproval() {
        RestaurantRegistrationDto dto = new RestaurantRegistrationDto();
        RestaurantRegistration registration = new RestaurantRegistration();
        when(mapping.registrationDtoToTableEntityMapping(dto)).thenReturn(registration);
        when(repository.save(registration)).thenReturn(registration);

        RestaurantRegistration result = service.registerRestaurantForApproval(dto);

        assertEquals(registration, result);
    }

    @Test
    void testFindRestaurantRegisterationOwnerIdByEmail_Found() {
        RestaurantRegistration registration = new RestaurantRegistration();
        registration.setId(1L);
        when(repository.findByOwnerEmail("test@example.com")).thenReturn(registration);
        assertEquals(1L, service.findRestaurantRegisterationOwnerIdByEmail("test@example.com"));
    }

    @Test
    void testFindRestaurantRegisterationOwnerIdByEmail_NotFound() {
        when(repository.findByOwnerEmail("test@example.com")).thenReturn(null);
        assertThrows(RegistrationUserNotFoundException.class, () -> {
            service.findRestaurantRegisterationOwnerIdByEmail("test@example.com");
        });
    }

    @Test
    void testOtpVerificationSuccessfulEnumChange() {
        RestaurantRegistration registration = new RestaurantRegistration();
        when(repository.findById(1L)).thenReturn(Optional.of(registration));
        service.otpVerificationSuccessfulEnumChange(1L);
        assertEquals(VerificationStatus.VERIFIED, registration.getOtpStatus());
        verify(repository, times(1)).save(registration);
    }
}
