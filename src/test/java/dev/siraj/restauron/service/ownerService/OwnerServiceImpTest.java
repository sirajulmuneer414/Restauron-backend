
package dev.siraj.restauron.service.ownerService;

import dev.siraj.restauron.entity.users.Owner;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.respository.ownerRepo.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnerServiceImpTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerServiceImp ownerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOwnerById_Found() {
        Owner owner = new Owner();
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        assertEquals(owner, ownerService.findOwnerById(1L));
    }

    @Test
    void testFindOwnerById_NotFound() {
        // The current implementation throws NoSuchElementException.
        // A better implementation would be to return Optional<Owner> from the service.
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            ownerService.findOwnerById(1L);
        });
    }

    @Test
    void testFindOwnerByUser_Found() {
        UserAll user = new UserAll();
        Owner owner = new Owner();
        when(ownerRepository.findByUser(user)).thenReturn(owner);
        assertEquals(owner, ownerService.findOwnerByUser(user));
    }

    @Test
    void testFindOwnerByUser_NotFound() {
        UserAll user = new UserAll();
        when(ownerRepository.findByUser(user)).thenReturn(null);
        assertNull(ownerService.findOwnerByUser(user));
    }
}
