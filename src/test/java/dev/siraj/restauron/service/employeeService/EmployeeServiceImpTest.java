
package dev.siraj.restauron.service.employeeService;

import dev.siraj.restauron.DTO.employee.PasswordChangeRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.DTO.owner.RestaurantReduxSettingDto;
import dev.siraj.restauron.DTO.owner.UpdateEmployeeRequestDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.employee.EmployeeMapping;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImpTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private IdEncryptionService idEncryptionService;

    @Mock
    private UserService userService;

    @Mock
    private EmployeeMapping employeeMapping;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImp employeeService;

    private UserAll user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        user = new UserAll();
        user.setEmail("test@test.com");
        user.setPassword("password");

        employee = new Employee();
        employee.setUser(user);
    }

    @Test
    void findRestaurantByEmployeeFromEncryptedID() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getName()).thenReturn("Test Restaurant");
        when(restaurant.getId()).thenReturn(1L);
        employee.setRestaurant(restaurant);

        when(idEncryptionService.decryptToLongId("encryptedId")).thenReturn(1L);
        when(userService.findUserById(1L)).thenReturn(user);
        when(employeeRepository.findByUser(user)).thenReturn(employee);
        when(idEncryptionService.encryptLongId(anyLong())).thenReturn("encryptedRestaurantId");

        RestaurantReduxSettingDto result = employeeService.findRestaurantByEmployeeFromEncryptedID("encryptedId");

        assertEquals("Test Restaurant", result.getRestaurantName());
        assertEquals("encryptedRestaurantId", result.getRestaurantEncryptedId());
    }

    @Test
    void getEmployeeDetailsById() {
        when(idEncryptionService.decryptToLongId("encryptedId")).thenReturn(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapping.mapToEmployeeViewDto(employee)).thenReturn(new EmployeeViewDto());

        EmployeeViewDto result = employeeService.getEmployeeDetailsById("encryptedId");

        assertNotNull(result);
    }

    @Test
    void getEmployeeDetailsById_notFound() {
        when(idEncryptionService.decryptToLongId("encryptedId")).thenReturn(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.getEmployeeDetailsById("encryptedId"));
    }

    @Test
    void updateEmployeeDetails() {
        UpdateEmployeeRequestDto updateDto = new UpdateEmployeeRequestDto();
        updateDto.setName("New Name");
        updateDto.setPhone("1234567890");
        updateDto.setPersonalEmail("new@test.com");

        when(idEncryptionService.decryptToLongId("encryptedId")).thenReturn(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapping.mapToEmployeeViewDto(employee)).thenReturn(new EmployeeViewDto());

        EmployeeViewDto result = employeeService.updateEmployeeDetails("encryptedId", updateDto);

        assertNotNull(result);
    }

    @Test
    void updateUserPassword() {
        PasswordChangeRequestDto passwordDto = new PasswordChangeRequestDto();
        passwordDto.setCurrentPassword("password");
        passwordDto.setNewPassword("newPassword");
        passwordDto.setConfirmNewPassword("newPassword");

        when(userService.findUserByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        employeeService.updateUserPassword("test@test.com", passwordDto);

        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void updateUserPassword_wrongPassword() {
        PasswordChangeRequestDto passwordDto = new PasswordChangeRequestDto();
        passwordDto.setCurrentPassword("wrongPassword");

        when(userService.findUserByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", "password")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateUserPassword("test@test.com", passwordDto));
    }
}
