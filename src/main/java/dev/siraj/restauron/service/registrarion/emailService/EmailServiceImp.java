package dev.siraj.restauron.service.registrarion.emailService;

import dev.siraj.restauron.service.registrarion.emailService.emailInterface.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmailId;


    @Override
    @Async
    public void sendEmail(String toEmailId, String body, String subject) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        if (toEmailId == null || !toEmailId.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email address: " + toEmailId);
        }

        simpleMailMessage.setFrom(fromEmailId);
        simpleMailMessage.setTo(toEmailId);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setSentDate(Date.from(Instant.now()));
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);


    }

    @Override
    @Async
    public void sendEmployeeCredentialsToEmail(String finalCompanyEmail, String generatedPassword, String personalEmail, String restaurantName) {

        log.info("Preparing to send credentials email to: {}", personalEmail);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true for multipart
            helper.setTo(personalEmail);
            helper.setSubject("Your New Restauron Employee Account Credentials");

            // You can use a more sophisticated HTML template here
            String emailContent = String.format(
                    "<html><body>" +
                            "<h2>Welcome to %s!</h2>" +
                            "<p>Your employee account for Restauron has been created.</p>" +
                            "<p>Please use the following credentials to log in:</p>" +
                            "<ul>" +
                            "<li><b>Email:</b> %s</li>" +
                            "<li><b>Temporary Password:</b> %s</li>" +
                            "</ul>" +
                            "<p>We recommend changing your password after your first login.</p>" +
                            "<br/>" +
                            "<p>Thank you,</p>" +
                            "<p>The Restauron Team</p>" +
                            "</body></html>",
                    restaurantName, finalCompanyEmail, generatedPassword
            );

            helper.setText(emailContent, true); // true to indicate HTML content

            javaMailSender.send(mimeMessage);
            log.info("Successfully sent credentials email to {}", personalEmail);

        } catch (MessagingException e) {
            log.error("Failed to send credentials email to {}: {}", personalEmail, e.getMessage());
        }
    }
}