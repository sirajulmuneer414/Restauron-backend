
package dev.siraj.restauron.service.registrarion.otpService;

import dev.siraj.restauron.DTO.registration.OtpDto;
import dev.siraj.restauron.entity.otpRegistration.OtpAndUser;
import dev.siraj.restauron.respository.otpRepository.OtpAndUserRepository;
import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OtpServiceImpTest {

    @Mock
    private EmailService emailService;

    @Mock
    private OtpAndUserRepository otpRepository;

    @InjectMocks
    private OtpServiceImp otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOtpForRestaurantRegistration() {
        String otp = otpService.sendOtpForRestaurantRegistration("Test User", "test@example.com", "Test Restaurant", 1L);
        assertNotNull(otp);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(otpRepository, times(1)).save(any(OtpAndUser.class));
    }

    @Test
    void testVerifyOtpUsingEmail_Success() {
        OtpDto otpDto = new OtpDto("123456", LocalDateTime.now());
        OtpAndUser otpAndUser = new OtpAndUser("123456", "test@example.com", LocalDateTime.now().plusMinutes(5));

        when(otpRepository.findByUserEmail("test@example.com")).thenReturn(otpAndUser);

        assertTrue(otpService.verifyOtpUsingEmail(otpDto, "test@example.com"));
    }

    @Test
    void testVerifyOtpUsingEmail_IncorrectOtp() {
        OtpDto otpDto = new OtpDto("654321", LocalDateTime.now());
        OtpAndUser otpAndUser = new OtpAndUser("123456", "test@example.com", LocalDateTime.now().plusMinutes(5));

        when(otpRepository.findByUserEmail("test@example.com")).thenReturn(otpAndUser);

        assertFalse(otpService.verifyOtpUsingEmail(otpDto, "test@example.com"));
    }

    @Test
    void testVerifyOtpUsingEmail_Expired() {
        OtpDto otpDto = new OtpDto("123456", LocalDateTime.now());
        OtpAndUser otpAndUser = new OtpAndUser("123456", "test@example.com", LocalDateTime.now().minusMinutes(1));

        when(otpRepository.findByUserEmail("test@example.com")).thenReturn(otpAndUser);

        assertFalse(otpService.verifyOtpUsingEmail(otpDto, "test@example.com"));
    }
}
