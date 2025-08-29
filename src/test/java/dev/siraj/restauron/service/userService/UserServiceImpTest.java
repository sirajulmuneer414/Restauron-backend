
package dev.siraj.restauron.service.userService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.entity.enums.Roles;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.admin.UserListMapping;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImpTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserListMapping userListMapping;

    @InjectMocks
    private UserServiceImp service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        UserAll user = new UserAll();
        service.saveUser(user);
        verify(repository, times(1)).save(user);
    }

    @Test
    void testUserExistsByEmailId() {
        when(repository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(service.userExistsByEmailId("test@example.com"));
    }

    @Test
    void testFindUserByEmail() {
        UserAll user = new UserAll();
        when(repository.findByEmail("test@example.com")).thenReturn(user);
        assertEquals(user, service.findUserByEmail("test@example.com"));
    }

    @Test
    void testFindAllUsersExceptAdminWithFilter_NoFilter() {
        PageRequestDto dto = new PageRequestDto();
        dto.setPageNo(0);
        dto.setSize(10);
        Page<UserAll> userPage = new PageImpl<>(Collections.singletonList(new UserAll()));
        when(repository.findByRoleNot(eq(Roles.ADMIN), any(Pageable.class))).thenReturn(userPage);
        service.findAllUsersExceptAdminWithFilter(dto);
        verify(repository, times(1)).findByRoleNot(eq(Roles.ADMIN), any(Pageable.class));
    }

    @Test
    void testFindUserById_Found() {
        UserAll user = new UserAll();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user, service.findUserById(1L));
    }

    @Test
    void testFindUserById_NotFound() {
        // The current implementation throws NoSuchElementException.
        // A better implementation would be to return Optional<UserAll> from the service.
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            service.findUserById(1L);
        });
    }
}
