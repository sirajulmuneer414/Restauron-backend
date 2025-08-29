
package dev.siraj.restauron.service.registrarion.restaurantRegistrationService;

import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.restaurant.RestaurantRegistration;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.mapping.restaurantRegistrationMapping.RestaurantRegistrationMapping;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestaurantRegistrationServiceImpTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantRegistrationMapping restaurantRegistrationMapping;

    @InjectMocks
    private RestaurantRegistrationServiceImp service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterRestaurantByRestaurantRegistrationDetailsAndOwner() {
        RestaurantRegistration registration = new RestaurantRegistration();
        Owner owner = new Owner();
        Restaurant restaurant = new Restaurant();

        when(restaurantRegistrationMapping.mapRestaurantRegistrationEntityToRestaurantEntity(registration, owner)).thenReturn(restaurant);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        Restaurant result = service.registerRestaurantByRestaurantRegistrationDetailsAndOwner(registration, owner);

        assertEquals(restaurant, result);
        verify(restaurantRegistrationMapping, times(1)).mapRestaurantRegistrationEntityToRestaurantEntity(registration, owner);
        verify(restaurantRepository, times(1)).save(restaurant);
    }
}
