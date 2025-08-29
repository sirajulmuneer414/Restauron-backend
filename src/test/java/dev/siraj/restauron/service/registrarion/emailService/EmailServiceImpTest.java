
package dev.siraj.restauron.service.registrarion.emailService;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceImpTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImp emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmail() {
        emailService.sendEmail("test@example.com", "Test Body", "Test Subject");
        verify(javaMailSender).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_InvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendEmail("invalid-email", "Test Body", "Test Subject");
        });
    }

    @Test
    void testSendEmployeeCredentialsToEmail() {
        MimeMessage mimeMessage = org.mockito.Mockito.mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.sendEmployeeCredentialsToEmail("company@example.com", "password", "personal@example.com", "Test Restaurant");
        verify(javaMailSender).send(mimeMessage);
    }
}
