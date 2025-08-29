
package dev.siraj.restauron.service.employeeManagementService;

import dev.siraj.restauron.DTO.common.PageRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeRegistrationRequestDto;
import dev.siraj.restauron.DTO.owner.EmployeeViewDto;
import dev.siraj.restauron.entity.restaurant.Restaurant;
import dev.siraj.restauron.entity.users.Employee;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.employee.EmployeeMapping;
import dev.siraj.restauron.respository.employeeRepo.EmployeeRepository;
import dev.siraj.restauron.respository.restaurantRepo.RestaurantRepository;
import dev.siraj.restauron.respository.userRepo.UserRepository;
import dev.siraj.restauron.service.encryption.idEncryption.IdEncryptionService;
import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeManagementServiceImpTest {

    @Mock
    private IdEncryptionService idEncryptionService;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private EmployeeMapping employeeMapping;

    @InjectMocks
    private EmployeeManagementServiceImp employeeManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee_Success() {
        EmployeeRegistrationRequestDto dto = new EmployeeRegistrationRequestDto();
        dto.setName("Test Employee");
        dto.setCompanyEmail("test@company.com");
        dto.setGeneratedPassword("password");

        when(idEncryptionService.decryptToLongId(anyString())).thenReturn(1L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(new Restaurant()));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserAll.class))).thenReturn(new UserAll());

        employeeManagementService.addEmployee(dto, "encryptedId");

        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        verify(emailService, times(1)).sendEmployeeCredentialsToEmail(anyString(), anyString(), anyString(), any());
    }

    @Test
    void testAddEmployee_RestaurantNotFound() {
        when(idEncryptionService.decryptToLongId(anyString())).thenReturn(1L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            employeeManagementService.addEmployee(new EmployeeRegistrationRequestDto(), "encryptedId");
        });
    }

    @Test
    void testFetchEmployees() {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPageNo(0);
        pageRequestDto.setSize(10);

        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(new Employee()));
        when(idEncryptionService.decryptToLongId(anyString())).thenReturn(1L);
        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(employeePage);
        when(employeeMapping.mapToEmployeeViewDto(any(Employee.class))).thenReturn(new EmployeeViewDto());

        Page<EmployeeViewDto> result = employeeManagementService.fetchEmployees(pageRequestDto, "encryptedId");

        assertEquals(1, result.getTotalElements());
    }
}
