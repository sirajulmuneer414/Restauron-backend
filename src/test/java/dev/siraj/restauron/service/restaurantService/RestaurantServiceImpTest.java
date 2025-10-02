
package dev.siraj.restauron.service.restaurantService;

import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.ownerService.interfaces.OwnerService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class RestaurantServiceImpTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private IdEncryptionService idEncryptionService;

    @Mock
    private OwnerService ownerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RestaurantServiceImp service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindRestaurantByRestaurantCode() {
        assertNull(service.findRestaurantByRestaurantCode("any-code"));
    }

    @Test
    void testFindRestaurantByOwner() {
        Owner owner = new Owner();
        Restaurant restaurant = new Restaurant();
        when(restaurantRepository.findByOwner(owner)).thenReturn(restaurant);
        assertEquals(restaurant, service.findRestaurantByOwner(owner));
    }

    @Test
    void testFindRestaurantByOwnerFromEncryptedId() {
        UserAll user = new UserAll();
        Owner owner = new Owner();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getName()).thenReturn("Test Restaurant");
        when(restaurant.getId()).thenReturn(1L);

        when(idEncryptionService.decryptToLongId("encrypted-id")).thenReturn(1L);
        when(userService.findUserById(1L)).thenReturn(user);
        when(ownerService.findOwnerByUser(user)).thenReturn(owner);
        when(restaurantRepository.findByOwner(owner)).thenReturn(restaurant);
        when(idEncryptionService.encryptLongId(1L)).thenReturn("encrypted-restaurant-id");

        RestaurantReduxSettingDto dto = service.findRestaurantByOwnerFromEncryptedId("encrypted-id");

        assertEquals("Test Restaurant", dto.getRestaurantName());
        assertEquals("encrypted-restaurant-id", dto.getRestaurantEncryptedId());
    }
}
