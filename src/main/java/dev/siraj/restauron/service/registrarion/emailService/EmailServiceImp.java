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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

    // Service implementation for sending emails

@Slf4j
@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmailId;


    /**
     * Send simple email
     *
     * @param toEmailId the to email id
     * @param body      the body
     * @param subject   the subject
     */
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

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String otp, String name, String restaurantName, String subject) {
        try {
            // 1. Prepare Context
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("name", name != null ? name : "User");
            context.setVariable("restaurantName", restaurantName); // Can be null

            // 2. Process Template
            String htmlContent = templateEngine.process("otp-template", context);

            // 3. Send HTML Email
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // TRUE triggers HTML mode

            javaMailSender.send(mimeMessage);
            log.info("Sent styled OTP email to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email", e);
            // Fallback to simple text if HTML fails? Or just throw.
        }
    }


    /**
     * Send employee credentials to email
     *
     * @param finalCompanyEmail the company email
     * @param generatedPassword the generated password
     * @param personalEmail     the personal email
     * @param restaurantName    the restaurant name
     */
    @Override
    @Async
    public void sendEmployeeCredentialsToEmail(String finalCompanyEmail, String generatedPassword, String personalEmail, String restaurantName) {

        log.info("Preparing to send credentials email to: {}", personalEmail);

        Context context = new Context();
        context.setVariable("restaurantName", restaurantName);
        context.setVariable("email", finalCompanyEmail);
        context.setVariable("password", generatedPassword);
        context.setVariable("loginLink", "http://localhost:5713/login");

        String processHtml = templateEngine.process("employee-credentials", context);

        try {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true for multipart
            helper.setTo(personalEmail);
            helper.setSubject("Welcome to "+ restaurantName + " - Your Employee Account Credentials");

            helper.setText(processHtml, true); // true to indicate HTML content

            javaMailSender.send(mimeMessage);
            log.info("Successfully sent credentials email to {}", personalEmail);

        } catch (MessagingException e) {
            log.error("Failed to send credentials email to {}: {}", personalEmail, e.getMessage());
        }
    }

    /**
     * Send subscription expired email to restaurant
     *
     * @param email the restaurant email
     */
    @Override
    @Async
    public void sendSubscriptionExpiredEmail(String email, String restaurantName) {

        Context context = new Context();

        context.setVariable("RestauronName", restaurantName);
        context.setVariable("restaurantEmail", email);
        context.setVariable("renewalLink", "http://localhost:5713/subscription/");
        String processHtml = templateEngine.process("subscription-expired", context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Your Restauron Subscription has Expired");
            helper.setText(processHtml, true); // true indicates HTML

            javaMailSender.send(mimeMessage);
            log.info("Subscription expiry email sent to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send subscription expiry email to {}: {}", email, e.getMessage());
        }

    }

    /**
     * Send subscription warning email to restaurant
     *
     * @param email the restaurant email
     * @param daysLeft number of days left before expiry
     */
    @Override
    @Async
    public void sendWarningEmail(String email, String restaurantName, int daysLeft) {

        Context context = new Context();
        context.setVariable("RestauronName", restaurantName);
        context.setVariable("restaurantEmail", email);
        context.setVariable("daysLeft", daysLeft);
        context.setVariable("renewalLink", "http://localhost:5713/subscription/");

        String expiryDate = LocalDate.now().plusDays(daysLeft).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        context.setVariable("expiryDate", expiryDate);

        String processHtml = templateEngine.process("subscription-warning", context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Your Restauron Subscription is Expiring Soon");
            helper.setText(processHtml, true); // true indicates HTML

            javaMailSender.send(mimeMessage);
            log.info("Subscription warning email sent to {} for {} days left", email, daysLeft);
        } catch (MessagingException e) {
            log.error("Failed to send subscription warning email to {}: {}", email, e.getMessage());
        }
    }


    /**
     * Send password reset email
     *
     * @param toEmail   the user email
     * @param resetLink the full reset link (e.g., http://localhost:5173/reset-password?token=XYZ)
     * @param userName  the user's name
     */
    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink, String userName) {
        log.info("Sending password reset email to: {}", toEmail);

        Context context = new Context();
        context.setVariable("name", userName != null ? userName : "User");
        context.setVariable("resetLink", resetLink);
        // Assuming you want a link that expires in 15 mins
        context.setVariable("expiryTime", "15 minutes");

        String htmlContent = templateEngine.process("password-reset", context);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Restauron - Password Reset Request");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Password reset email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}