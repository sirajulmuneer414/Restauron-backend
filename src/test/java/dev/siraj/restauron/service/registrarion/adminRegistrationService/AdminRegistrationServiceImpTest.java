
package dev.siraj.restauron.service.registrarion.adminRegistrationService;

import dev.siraj.restauron.DTO.registration.AdminRegistrationDto;
import dev.siraj.restauron.entity.enums.AccountStatus;
import dev.siraj.restauron.entity.users.Admin;
import dev.siraj.restauron.entity.users.UserAll;
import dev.siraj.restauron.mapping.adminRegistrationMapping.AdminRegistrationMapping;
import dev.siraj.restauron.respository.adminRepo.AdminRepository;
import dev.siraj.restauron.service.registrarion.otpService.otpInterface.OtpService;
import dev.siraj.restauron.service.userService.UserServiceInterface.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminRegistrationServiceImpTest {

    @Mock
    private AdminRegistrationMapping mapping;
    @Mock
    private UserService userService;
    @Mock
    private AdminRepository repository;
    @Mock
    private OtpService otpService;

    @InjectMocks
    private AdminRegistrationServiceImp adminRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVerifyAdminCode() {
        assertTrue(adminRegistrationService.verifyAdminCode("KJ9MP4XN7QRTY2VL8WZ3"));
        assertFalse(adminRegistrationService.verifyAdminCode("wrong_code"));
    }

    @Test
    void testRegisterAdminAndSentOtpForVerification_Success() throws Exception {
        AdminRegistrationDto dto = new AdminRegistrationDto();
        UserAll user = new UserAll();
        Admin admin = new Admin();

        when(mapping.adminRegistrationDtoToUserAllClassMapping(dto)).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);
        when(mapping.adminRegistrationDtoToAdminMapping(dto, user)).thenReturn(admin);
        when(repository.save(admin)).thenReturn(admin);
        when(otpService.sendOtpForAdminRegistration(any(), any(), any())).thenReturn("123456");

        assertTrue(adminRegistrationService.registerAdminAndSentOtpForVerification(dto));
    }

    @Test
    void testRegisterAdminAndSentOtpForVerification_OtpFailure() throws Exception {
        AdminRegistrationDto dto = new AdminRegistrationDto();
        UserAll user = new UserAll();
        Admin admin = new Admin();

        when(mapping.adminRegistrationDtoToUserAllClassMapping(dto)).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);
        when(mapping.adminRegistrationDtoToAdminMapping(dto, user)).thenReturn(admin);
        when(repository.save(admin)).thenReturn(admin);
        when(otpService.sendOtpForAdminRegistration(any(), any(), any())).thenThrow(new RuntimeException());

        assertFalse(adminRegistrationService.registerAdminAndSentOtpForVerification(dto));
    }

    @Test
    void testFindAdminByUserAll() {
        UserAll user = new UserAll();
        Admin admin = new Admin();
        when(repository.findByUser(user)).thenReturn(admin);
        assertEquals(admin, adminRegistrationService.findAdminByUserAll(user));
    }

    @Test
    void testOtpVerificationSuccessfulEnumChange() {
        Admin admin = new Admin();
        adminRegistrationService.otpVerificationSuccessfulEnumChange(admin);
        assertEquals(AccountStatus.ACTIVE, admin.getAdminStatus());
        verify(repository, times(1)).save(admin);
    }

    @Test
    void testSaveAdmin() {
        Admin admin = new Admin();
        adminRegistrationService.saveAdmin(admin);
        verify(repository, times(1)).save(admin);
    }
}
