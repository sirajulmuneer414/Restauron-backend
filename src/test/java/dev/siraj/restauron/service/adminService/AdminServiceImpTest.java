
package dev.siraj.restauron.service.adminService;

import dev.siraj.restauron.entity.enums.PendingStatuses;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.service.registrarion.ownerRegistrationService.OwnerRegistrationService;
import dev.siraj.restauron.service.registrarion.registrationInitialService.registrationInitialInterface.RestaurantInitialService;
import dev.siraj.restauron.service.registrarion.restaurantRegistrationService.RestaurantRegistrationService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImpTest {

    @Mock
    private RestaurantInitialService restaurantInitialService;

    @Mock
    private RestaurantRegistrationMapping restaurantRegistrationMapping;

    @Mock
    private RestaurantRegistrationService restaurantRegistrationService;

    @Mock
    private UserService userService;

    @Mock
    private OwnerRegistrationService ownerRegistrationService;

    @InjectMocks
    private AdminServiceImp adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateRestaurantRegistrationStatusAndSaveRestaurantAndOwner_Approved() {
        Long restaurantId = 1L;
        String statusUpdateTo = PendingStatuses.APPROVED.name();
        RestaurantRegistration restaurantRegistration = new RestaurantRegistration();
        UserAll user = new UserAll();
        Owner owner = new Owner();

        when(restaurantInitialService.findRestaurantRegistrationById(restaurantId)).thenReturn(restaurantRegistration);
        when(restaurantRegistrationMapping.restaurantRegistrationToUserForOwner(restaurantRegistration)).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);
        when(ownerRegistrationService.mapToOwnerAndSave(user, restaurantRegistration)).thenReturn(owner);

        boolean result = adminService.updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(restaurantId, statusUpdateTo);

        assertTrue(result);
        verify(restaurantInitialService, times(1)).findRestaurantRegistrationById(restaurantId);
        verify(restaurantRegistrationMapping, times(1)).restaurantRegistrationToUserForOwner(restaurantRegistration);
        verify(userService, times(1)).saveUser(user);
        verify(ownerRegistrationService, times(1)).mapToOwnerAndSave(user, restaurantRegistration);
        verify(restaurantRegistrationService, times(1)).registerRestaurantByRestaurantRegistrationDetailsAndOwner(restaurantRegistration, owner);
        verify(restaurantInitialService, times(1)).deleteRestaurantRegistrationOnApproval(restaurantRegistration);
    }

    @Test
    void testUpdateRestaurantRegistrationStatusAndSaveRestaurantAndOwner_Rejected() {
        Long restaurantId = 1L;
        String statusUpdateTo = PendingStatuses.REJECTED.name();
        RestaurantRegistration restaurantRegistration = new RestaurantRegistration();

        when(restaurantInitialService.findRestaurantRegistrationById(restaurantId)).thenReturn(restaurantRegistration);

        boolean result = adminService.updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(restaurantId, statusUpdateTo);

        assertTrue(result);
        verify(restaurantInitialService, times(1)).findRestaurantRegistrationById(restaurantId);
        verify(restaurantInitialService, times(1)).rejectedByAdmin(restaurantRegistration);
    }

    @Test
    void testUpdateRestaurantRegistrationStatusAndSaveRestaurantAndOwner_InvalidStatus() {
        Long restaurantId = 1L;
        String statusUpdateTo = "INVALID_STATUS";

        boolean result = adminService.updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(restaurantId, statusUpdateTo);

        assertFalse(result);
    }

    @Test
    void testUpdateRestaurantRegistrationStatusAndSaveRestaurantAndOwner_Exception() {
        Long restaurantId = 1L;
        String statusUpdateTo = PendingStatuses.APPROVED.name();

        when(restaurantInitialService.findRestaurantRegistrationById(restaurantId)).thenThrow(new RuntimeException("Test Exception"));

        boolean result = adminService.updateRestaurantRegistrationStatusAndSaveRestaurantAndOwner(restaurantId, statusUpdateTo);

        assertFalse(result);
    }
}
