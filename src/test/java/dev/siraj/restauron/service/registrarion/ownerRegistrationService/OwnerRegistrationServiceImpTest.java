
package dev.siraj.restauron.service.registrarion.ownerRegistrationService;

import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.owner.OwnerUserMapping;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OwnerRegistrationServiceImpTest {

    @Mock
    private OwnerUserMapping ownerUserMapping;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerRegistrationServiceImp ownerRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapToOwnerAndSave() {
        UserAll userAll = new UserAll();
        RestaurantRegistration restaurantRegistration = new RestaurantRegistration();
        Owner owner = new Owner();

        when(ownerUserMapping.mapToOwnerFromUserAllAndRestaurantRegistration(userAll, restaurantRegistration)).thenReturn(owner);
        when(ownerRepository.save(owner)).thenReturn(owner);

        Owner result = ownerRegistrationService.mapToOwnerAndSave(userAll, restaurantRegistration);

        assertEquals(owner, result);
        verify(ownerUserMapping, times(1)).mapToOwnerFromUserAllAndRestaurantRegistration(userAll, restaurantRegistration);
        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    void testFindOwnerByUserAll() {
        UserAll user = new UserAll();
        Owner owner = new Owner();
        when(ownerRepository.findByUser(user)).thenReturn(owner);
        assertEquals(owner, ownerRegistrationService.findOwnerByUserAll(user));
    }
}
